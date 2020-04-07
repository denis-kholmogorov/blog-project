package main.services.apiGeneralSevice;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class ApiGeneralServiceImpl implements ApiGeneralService
{

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
    public InitDto init()
    {
        return initDto;
    }

    @Override
    public ListTagsDto findTagsByQuery(String queryParam)
    {
        String query = queryParam.toLowerCase();
        List<TagDto> list = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        double maxWeight = list.get(0).getWeight();
        list.forEach(tagDto -> tagDto.setWeight(tagDto.getWeight()/maxWeight));
        if(!query.isEmpty()){
            list = list.stream().filter(t-> t.getName().toLowerCase().contains(query)).collect(Collectors.toList());
        }
        return new ListTagsDto(list);
    }

    @Override
    public ResponseCalendarDto getAllPostByCalendar(Integer year)
    {
        List<String> listDateAndCount;
        Map<String, Integer> map = new HashMap<>();
        List<Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        ResponseCalendarDto responseCalendarDto = new ResponseCalendarDto();
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        listDateAndCount = postRepository.findCountPostForCalendar(year);
        listDateAndCount.forEach(s->{
            int a = s.indexOf(',');
            map.put(s.substring(0,a),(Integer.valueOf(s.substring(a+1))));
        });
        responseCalendarDto.setPosts(map);
        responseCalendarDto.setYears(allYearsWithPosts);
        return responseCalendarDto;
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

    public ResponseEntity<?> setComment(RequestCommentsDto commentDto, HttpSession session){
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
                    log.info(comment.getId() + "");
                    Integer commentId = commentsRepository.save(comment).getId();
                    log.info(post.getComments().size() + " количество комментаариев");

                    log.info("New comment has been added with id " + commentId);
                    return ResponseEntity.ok(new AnswerComentDto(commentId));

                }
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            else
                {
                Map<String, String> error = new HashMap<>();
                error.put("text", "Текст комментария не задан или слишком короткий");
                return ResponseEntity.ok(new AnswerErrorDto(false, error));
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
        AnswerErrorDto errors = new AnswerErrorDto();
        if(providerToken.validateToken(session.getId())) {
            Optional<User> optional = userRepository.findById(providerToken.getUserIdBySession(session.getId()));
            if(optional.isEmpty()) throw new BadRequestException("User not found");
            User user = optional.get();
            if (profileDto.getPassword() != null && profileDto.getPassword().length() >= 6){
                user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
            }
            else if (profileDto.getPassword() != null && profileDto.getPassword().length() > 0 && profileDto.getPassword().length() < 6) {
                log.info(profileDto.getPassword() + " password");
                errors.getErrors().put("password", "Пароль короче 6-ти символов");
                return ResponseEntity.ok(errors);
            }

            if(profileDto.getName().matches("\\w+.?\\w+") && profileDto.getName().length() > 2){
                user.setName(profileDto.getName());
            }else {
                errors.getErrors().put("name","Имя указано неверно");
            }

            if(profileDto.getEmail() != null) {
                String emailUser = null;

                if (!userRepository.existsByEmail(profileDto.getEmail())) {
                    user.setEmail(profileDto.getEmail());
                }
                else if (user.getEmail().equals(profileDto.getEmail())){
                    log.info("Email " + profileDto.getEmail() + " не изменен");
                }else{
                    errors.getErrors().put("email", "Этот e-mail уже зарегистрирован");
                    return ResponseEntity.ok(errors);
                }
            }
            if(profileDto.getRemovePhoto() != null){
                File file = new File(user.getPhoto());
                file.delete();
                if(profileDto instanceof RequestProfileWithPhotoDto){
                    if(((RequestProfileWithPhotoDto)profileDto).getPhoto()!= null){
                        try {
                            user.setPhoto(loadAvatar(((RequestProfileWithPhotoDto)profileDto).getPhoto().getBytes()));
                            log.info("Картинка загрузилась");
                        } catch (IOException e) {
                            log.warn("Файл не загружен");
                        }
                    }
                }
            }
            User userSaved = userRepository.save(user);
            log.info(userSaved.toString() + " сохранен");
            return ResponseEntity.ok(new AnswerDto(true));
        }
        return null;
    }

    public String loadAvatar(byte[] image) throws IOException {

        String path = "src/main/resources/static/img/";
        String imageName = "avatar.087cb69a";
        String pathImage = path + imageName + ".jpg";
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(bais);
        ImageIO.write(bi, "jpg", new File(pathImage));
        return "avatar.jpg";

    }
}
