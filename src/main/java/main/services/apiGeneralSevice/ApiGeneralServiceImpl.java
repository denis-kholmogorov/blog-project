package main.services.apiGeneralSevice;

import lombok.extern.log4j.Log4j2;
import main.CustomException.BadRequestException;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestCommentsDto;
import main.DTOEntity.request.RequestProfileDto;
import main.DTOEntity.request.RequestProfileWithPhotoDto;
import main.DTOEntity.response.ResponseCalendarDto;
import main.model.*;
import main.repositories.*;
import main.security.ProviderToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ApiGeneralServiceImpl implements ApiGeneralService {

    private final TagRepository tagRepository;

    private final PostRepository postRepository;

    private final GlobalSettingsRepository globalSettingsRepository;

    private final ProviderToken providerToken;

    private final UserRepository userRepository;

    private final PostCommentsRepository commentsRepository;

    private final PasswordEncoder passwordEncoder;

    private final InitDto initDto;


    @Autowired
    public ApiGeneralServiceImpl(TagRepository tagRepository,
                                 PostRepository postRepository,
                                 GlobalSettingsRepository globalSettingsRepository,
                                 ProviderToken providerToken,
                                 UserRepository userRepository,
                                 PostCommentsRepository commentsRepository,
                                 PasswordEncoder passwordEncoder,
                                 InitDto initDto) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.providerToken = providerToken;
        this.userRepository = userRepository;
        this.commentsRepository = commentsRepository;
        this.passwordEncoder = passwordEncoder;
        this.initDto = initDto;
    }

    @Override
    public InitDto init() {
        return initDto;
    }

    @Override
    public ListTagsDto findTagsByQuery(String queryParam) {
        String query = queryParam.toLowerCase();
        List<TagDto> list = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        if (list.size() > 0) {
            double maxWeight = list.get(0).getWeight();
            list.forEach(tagDto -> tagDto.setWeight(tagDto.getWeight() / maxWeight));
            if (!query.isEmpty()) {
                list = list.stream().filter(t -> t.getName().toLowerCase().contains(query)).collect(Collectors.toList());
            }
        }
        return new ListTagsDto(list);
    }

    @Override
    public ResponseCalendarDto getAllPostByCalendar(Integer year) {
        List<String> listDateAndCount;
        Map<String, Integer> map = new HashMap<>();
        List<Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        ResponseCalendarDto responseCalendarDto = new ResponseCalendarDto();
        if (year == null) year = LocalDateTime.now().getYear();
        listDateAndCount = postRepository.findCountPostForCalendar(year);
        listDateAndCount.forEach(s -> {
            int a = s.indexOf(',');
            map.put(s.substring(0, a), (Integer.valueOf(s.substring(a + 1))));
        });
        responseCalendarDto.setPosts(map);
        responseCalendarDto.setYears(allYearsWithPosts);
        return responseCalendarDto;
    }

    @Override
    public StatisticsBlogDto getAllStatistics() {
        return new StatisticsBlogDto(postRepository.findAllStatistics());
    }

    public Optional<GlobalSettings> getSettingIsPublic() {
        return globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
    }

    public Map<String, Boolean> getSettings(String sessionId) {

        if (providerToken.validateToken(sessionId)) {
            Map<String, Boolean> settings = globalSettingsRepository.findOnlyCodeAndValue();
            return settings;
        }
        return null;
    }

    public boolean setSettings(Map<String, Boolean> settings, String sessionId) {
        if (userRepository.findById(providerToken.getUserIdBySession(sessionId)).get().getIsModerator() == (byte) 1) {
            List<GlobalSettings> listSettingsFromBD = globalSettingsRepository.findAllSettings();
            for (String code : settings.keySet()) {
                log.info(code + " номер кода настройки для изменения");
                listSettingsFromBD.forEach(gs -> {
                    if (code.equals(gs.getCode())) {
                        log.info("Изменение настройки " + code);
                        gs.setValue(settings.get(code));
                    }
                });
            }
            globalSettingsRepository.saveAll(listSettingsFromBD);
            return true;
        }
        throw new BadRequestException("Юзер не является модератором");
    }

    public AnswerDto setModerationDecision(ModerationDecisionDto decision, String session) {

        Optional<User> optionalUser = userRepository.findById(providerToken.getAuthUserIdBySession(session));
        User user = optionalUser.get();
        if (user.getIsModerator() == 1) {
            Post post = postRepository.findById(decision.getPost_id()).orElse(null);
            if (post != null && decision.getDecision().toLowerCase().equals("accept")) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
                post.setModeratorId(user.getId());
            } else if (post != null && decision.getDecision().toLowerCase().equals("decline")) {
                post.setModerationStatus(ModerationStatus.DECLINED);
                post.setModeratorId(user.getId());
            }
            assert post != null;
            Calendar time = post.getTime();
            time.add(Calendar.HOUR, -3);
            post.setTime(time);
            postRepository.save(post);
            log.info(post.getModeratorId() + " " + post.getModerationStatus());
        }
        return new AnswerDto(true);
    }

    public ResponseEntity<?> setComment(RequestCommentsDto commentDto, HttpSession session) {
        providerToken.getAuthUserIdBySession(session.getId());
        if (commentDto.getText().length() > 10) {
            Post post = postRepository.findById(commentDto.getPostId()).orElse(null);
            if (post == null) throw new BadRequestException("Пост не найден");
            PostComments comment = new PostComments();
            comment.setText(commentDto.getText());
            if (commentDto.getParentId() != null) {

                if (!commentsRepository.existsById(commentDto.getParentId())) throw new BadRequestException("Коммент не существует");
                comment.setParentId(commentDto.getParentId());
            }
            Optional<User> userOptional = userRepository.findById(providerToken.getAuthUserIdBySession(session.getId()));
            comment.setUser(userOptional.get());
            post.getComments().add(comment);
            comment.setPost(post);
            Integer commentId = commentsRepository.save(comment).getId();
            postRepository.save(post);
            log.info("Комментарий с id {} добавлен к посту {}",commentId, post.getId());
            return ResponseEntity.ok(new AnswerComentDto(commentId));
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("text", "Текст комментария не задан или слишком короткий");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AnswerErrorDto(false, error));
        }
    }

    public StatisticsBlogDto getMyStatistics(String sessionId) {
        Integer userId = providerToken.getAuthUserIdBySession(sessionId);
        return new StatisticsBlogDto(postRepository.findAllStatisticsById(userId));
    }

    public ResponseEntity<?> setMyProfile(RequestProfileDto profileDto, HttpSession session) {
        AnswerErrorDto errors = new AnswerErrorDto();
        if (providerToken.validateToken(session.getId())) {
            Optional<User> optional = userRepository.findById(providerToken.getAuthUserIdBySession(session.getId()));
            if (optional.isEmpty()) throw new BadRequestException("User not found");
            User user = optional.get();
            if (profileDto.getPassword() != null && profileDto.getPassword().length() >= 6) {
                user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
            } else if (profileDto.getPassword() != null && profileDto.getPassword().length() > 0 && profileDto.getPassword().length() < 6) {
                log.info(profileDto.getPassword() + " password");
                errors.getErrors().put("password", "Пароль короче 6-ти символов");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            if (profileDto.getName() != null && profileDto.getName().matches("[a-zA-ZА-Яа-я]+.?[a-zA-ZА-Яа-я]+")
                    && profileDto.getName().length() > 2) {
                user.setName(profileDto.getName());
            } else {
                errors.getErrors().put("name", "Имя указано неверно");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            if (profileDto.getEmail() != null) {
                String emailUser = null;
                if (!userRepository.existsByEmail(profileDto.getEmail())) {
                    log.info("Пользователь c email " + user.getEmail() +
                            " изменил email на " + profileDto.getEmail());
                    user.setEmail(profileDto.getEmail());
                } else if (user.getEmail().equals(profileDto.getEmail())) {
                    log.info("Email " + profileDto.getEmail() + " не изменен");
                } else {
                    errors.getErrors().put("email", "Этот e-mail уже зарегистрирован");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
                }
            }
            if (profileDto.getRemovePhoto() != null) {
                File file = new File(user.getPhoto());
                file.delete();
                if (profileDto instanceof RequestProfileWithPhotoDto) {
                    if (((RequestProfileWithPhotoDto) profileDto).getPhoto() != null) {
                        try {
                            user.setPhoto(loadAvatar(((RequestProfileWithPhotoDto) profileDto).getPhoto().getBytes()));
                            log.info("Фото изменено");
                        } catch (IOException e) {
                            log.warn("Фото не загружено");
                        }
                    }
                }
            }
            userRepository.save(user);
            return ResponseEntity.ok(new AnswerDto(true));
        }
        return null;
    }


    public String loadFile(byte[] image) throws IOException {
        String path = "src/main/resources/static/img/upload/";
        String imageName = "avatar.087cb69a.jpg";
        File file = new File(path);
        file.mkdirs();
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(bais);
        ImageIO.write(bi, "jpg", new File(path + imageName));
        return "img/upload/" + imageName;
       /* StringBuilder generatedString = null;
        if (image.length != 0) {
            generatedString = createRandomDirectories();
        }
        String pathAnswer = generatedString.insert(0, "upload").toString();
        String dirs = generatedString.substring(0, generatedString.lastIndexOf("/"));
        String imageName = generatedString.substring(generatedString.lastIndexOf("/"));
        File file = new File(dirs);
        file.mkdirs();
        String pathImage = file + imageName + ".jpeg";

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            BufferedImage bi = ImageIO.read(bais);
            ImageIO.write(bi, "jpeg", new File(pathImage));
            return "img/upload" + pathAnswer + ".jpeg";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }


    public String loadAvatar(byte[] image) throws IOException {
        StringBuilder generatedString = null;
        if (image.length != 0) {
            generatedString = createRandomDirectories();
        }
        String path = "src/main/resources/static/img/avatars/";
        String imageName = "avatar.087cb69a.jpg";
        //String pathImage = path + generatedString.toString() + imageName + ".jpg";
        File file = new File(path);
        file.mkdirs();
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(bais);
        ImageIO.write(bi, "jpg", new File(path + imageName));
        return "img/avatars/" + imageName;
    }

    public StringBuilder createRandomDirectories() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 6;
        Random random = new Random();

        StringBuilder generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

        for (int i = 0; i < 10; i = i + 3) {
            generatedString.insert(i, "/");
        }
        return generatedString;
    }
}