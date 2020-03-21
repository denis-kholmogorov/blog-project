package main.services.apiGeneralSevice;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiGeneralServiceImpl implements ApiGeneralService
{

    TagRepository tagRepository;

    PostRepository postRepository;

    GlobalSettingsRepository globalSettingsRepository;

    ProviderToken providerToken;

    UserRepository userRepository;

    PostCommentsRepository commentsRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public ApiGeneralServiceImpl(TagRepository tagRepository,
                                 PostRepository postRepository,
                                 GlobalSettingsRepository globalSettingsRepository,
                                 ProviderToken providerToken,
                                 UserRepository userRepository,
                                 PostCommentsRepository commentsRepository,
                                 PasswordEncoder passwordEncoder) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.providerToken = providerToken;
        this.userRepository = userRepository;
        this.commentsRepository = commentsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public InitDto init()
    {
        List<String> list = null;
        try
        {
            list = Files.readAllLines(Paths.get("copyright.txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InitDto(list.get(0), list.get(1), list.get(2),
                list.get(3), list.get(4), list.get(5));
    }

    @Override
    public ListTagsDto findTagsByQuery(String query)
    {
        query.toLowerCase();
        List<TagDto> list = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        double maxWeight = list.get(0).getWeight();
        list.forEach(tagDto -> tagDto.setWeight(tagDto.getWeight()/maxWeight));
        if(!query.isEmpty())
        {
            list = list.stream().filter(t-> t.getName().toLowerCase().contains(query)).collect(Collectors.toList());
        }
        return new ListTagsDto(list);
    }

    @Override
    public CalendarDto getAllPostByCalendar(Integer year)
    {
        List<String> listDateAndCount;
        Map<String, Integer> map = new HashMap<>();
        List<Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        CalendarDto calendarDto = new CalendarDto();

        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        listDateAndCount = postRepository.findCountPostForCalendar(year);
        listDateAndCount.forEach(s->{
            int a = s.indexOf(',');
            map.put(s.substring(0,a),(Integer.valueOf(s.substring(a+1))));
        });
        calendarDto.setPosts(map);
        calendarDto.setYears(allYearsWithPosts);

        return calendarDto;
    }

    @Override
    public StatisticsBlogDto getAllStatistics()
    {
        return new StatisticsBlogDto(postRepository.findAllStatistics());
    }


    public Optional<GlobalSettings> getSettingIsPublic(){ return globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");}

    public String loadFile(byte[] image){

        if(image.length != 0){
            int leftLimit = 48;
            int rightLimit = 122;
            int targetStringLength = 12;
            Random random = new Random();

            new StringBuilder();
            StringBuilder generatedString =random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

            for (int i = 0; i < 10; i = i + 3){
                generatedString.insert(i,"/");
            }

            generatedString.insert(0,"src/main/resources/static/img/");
            String dirs = generatedString.substring(0, generatedString.lastIndexOf("/"));
            String imageName = generatedString.substring(generatedString.lastIndexOf("/"));
            File file = new File(dirs);
            file.mkdirs();
            String pathImage = file + imageName + ".jpeg";

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(image);
                BufferedImage bi = ImageIO.read(bais);
                ImageIO.write(bi, "jpeg",new File(pathImage));
                return pathImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, Boolean> getSettings(String sessionId){
        log.info("Работаем внутри getsettings");
        if(providerToken.validateToken(sessionId) &&
                userRepository.findById(providerToken.getUserIdBySession(sessionId)).get().getIsModerator() == (byte)1)
        {

            Map<String, Boolean> settings = globalSettingsRepository.findOnlyCodeAndValue();
            log.info("Получил настройки " + settings.size());
            return settings;
        }
        log.info("не отработал get_settings");
        return null;
    }

    public boolean setSettings(Map<String, Boolean> settings, String sessionId){
        log.info((userRepository.findById(providerToken.getUserIdBySession(sessionId))
                .get()
                .getIsModerator() == (byte)1) + " найден модератор");
        log.info(providerToken.validateToken(sessionId) + " прошел валидацию");

        if(providerToken.validateToken(sessionId) &&
                userRepository.findById(providerToken.getUserIdBySession(sessionId)).get().getIsModerator() == (byte)1)
        {
            List<GlobalSettings> listSettingsFromBD = globalSettingsRepository.findAllSettings();
            log.info(listSettingsFromBD.size() + " size list settings");
            for(String code: settings.keySet()){
                log.info(code + " номер кода");
                listSettingsFromBD.forEach(gs->{
                    if(code.equals(gs.getCode())){
                        log.info("Changed value setting " + code);
                        gs.setValue(settings.get(code));
                    }

                });
            }
            globalSettingsRepository.saveAll(listSettingsFromBD);
            return true;
        }
        return false;
    }

    public AnswerDto setModerationDecision(ModerationDecisionDto decision, String session){
        if(providerToken.validateToken(session)){
            Optional<User> optionalUser = userRepository.findById(providerToken.getUserIdBySession(session));
            if(optionalUser.isEmpty()){
                return new AnswerDto(false);
            }
            User user = optionalUser.get();
            if(user.getIsModerator() == 1){
                Post post = postRepository.findById(decision.getPost_id()).orElse(null);
                if(post != null && decision.getDecision().toLowerCase().equals("accept")){
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                    post.setModeratorId(user.getId());
                }
                else if(post != null && decision.getDecision().toLowerCase().equals("decline")){
                    post.setModerationStatus(ModerationStatus.DECLINED);
                    post.setModeratorId(user.getId());
                }
                assert post != null;
                postRepository.save(post);
                log.info(post.getModeratorId() + " " + post.getModerationStatus());
            }
        }
        return new AnswerDto(true);
    }

    public ResponseEntity<?>  setComment(RequestCommentsDto commentDto, HttpSession session){
        if(providerToken.validateToken(session.getId())) {
            if (commentDto.getText().length() > 10)
            {
                Post post = postRepository.findById(commentDto.getPostId()).orElse(null);
                if (post != null)
                {
                        PostComments comment = new PostComments();
                        comment.setComment(commentDto.getText());
                        comment.setPost(post);

                        if(commentDto.getParentId() != null)
                        {
                            User parentUser = userRepository.findById(commentDto.getParentId()).orElse(null);
                            if(parentUser == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                            comment.setParentId(commentDto.getParentId());
                        }
                        Optional<User> userOptional = userRepository.findById(providerToken.getUserIdBySession(session.getId()));
                        if(userOptional.isPresent()){
                            comment.setUser(userOptional.get());
                        }else{
                            log.info("Юзер ненайден");
                        }
                        Integer commentId = commentsRepository.save(comment).getId();
                        log.info("New comment has been added with id " + commentId);
                        return ResponseEntity.ok(new AnswerComentDto(commentId));

                }
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            else
                {
                Map<String, String> error = new HashMap<>();
                error.put("text", "Текст комментария не задан или слишком короткий");
                    log.info("Post has length less 10 symbols ");
                return ResponseEntity.ok(new ErrorAnswerDto(false, error));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    
    public StatisticsBlogDto getMyStatistics (String sessionId){
        if(providerToken.validateToken(sessionId)){
            Integer userId = providerToken.getUserIdBySession(sessionId);
           return new StatisticsBlogDto(postRepository.findAllStatisticsById(userId));
        }
        return null;
    }

    public ResponseEntity<?> setMyProfile(RequestProfileDto profileDto, HttpSession session){
        ErrorAnswerDto errors = new ErrorAnswerDto();
        if(providerToken.validateToken(session.getId())) {
            Optional<User> optional = userRepository.findById(providerToken.getUserIdBySession(session.getId()));
            if(optional.isPresent())
            {
                User user = optional.get();

                if (profileDto.getPassword() != null && profileDto.getPassword().length() >= 6){
                    user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
                }
                else if(profileDto.getPassword().length() > 0 && profileDto.getPassword().length() < 6){
                    log.info(profileDto.getPassword() + " password");
                    errors.getErrors().put("password","Пароль короче 6-ти символов");
                    return ResponseEntity.ok(errors);
                }

                if(profileDto.getName().matches("\\w+") && profileDto.getName().length() > 2){
                    user.setName(profileDto.getName());
                }else {
                    errors.getErrors().put("name","Имя указано неверно");
                }

                if(userRepository.findByEmail(profileDto.getEmail()).isEmpty()){
                    user.setEmail(profileDto.getEmail());
                }else if (!profileDto.getEmail().isEmpty() && profileDto.getEmail().length() > 1){
                    errors.getErrors().put("email","Этот e-mail уже зарегистрирован");
                    return ResponseEntity.ok(errors);
                }

                if(profileDto.getRemovePhoto() != null){
                    File file = new File(user.getPhoto());
                    if(file.delete()) log.info("Изображение удалено");// удалить

                    if(profileDto instanceof RequestProfileWithPhotoDto){
                        if(((RequestProfileWithPhotoDto)profileDto).getPhoto()!= null){
                            try {
                                user.setPhoto(loadAvatar(((RequestProfileWithPhotoDto)profileDto).getPhoto().getBytes()));
                                log.info("Картинка загрузилась");
                            } catch (IOException e) {
                                System.out.println("Файл не загружен");
                            }
                        }
                    }
                }
                User userSaved = userRepository.save(user);
                log.info(userSaved.toString() + " сохранен");
                return ResponseEntity.ok(new AnswerDto(true));
            }
        }
        return null;
    }

    public String loadAvatar(byte[] image) throws IOException {

        String path = "src/main/resources/static/img/";
        String imageName = "avatarkka";
        log.info(imageName + " имя файла");
        String pathImage = path + imageName + ".jpg";
        log.info(pathImage + " путь файла");
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(bais);
        ImageIO.write(bi, "jpg", new File(pathImage));
        return imageName + ".jpg";

    }
}
