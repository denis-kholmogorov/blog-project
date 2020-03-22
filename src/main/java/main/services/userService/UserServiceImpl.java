package main.services.userService;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.AnswerDto;
import main.DTOEntity.AnswerLoginDto;
import main.DTOEntity.ErrorAnswerDto;
import main.DTOEntity.UserLoginDto;
import main.DTOEntity.request.RequestRestoreDto;
import main.DTOEntity.request.RequestSetPasswordDto;
import main.model.CaptchaCodes;
import main.model.User;
import main.repositories.CaptchaCodesRepository;
import main.repositories.UserRepository;
import main.security.ProviderToken;
import main.services.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{

    @Value("${intervalCountTime}")
    String intervalCount;

    private UserRepository userRepository;

    private CaptchaCodesRepository codesRepository;

    private PasswordEncoder passwordEncoder;

    private ProviderToken providerToken;

    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CaptchaCodesRepository codesRepository,
                           PasswordEncoder passwordEncoder,
                           ProviderToken providerToken,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.codesRepository = codesRepository;
        this.passwordEncoder = passwordEncoder;
        this.providerToken = providerToken;
        this.emailService = emailService;
    }

    @Override
    public ErrorAnswerDto registerUser(String email, String password, String captcha, String captcha_secret) {
        ErrorAnswerDto errorAnswer = new ErrorAnswerDto();
        Optional<CaptchaCodes> optionalCaptcha = codesRepository.findByCode(captcha);
        if (password.length() >= 6) {
            if (optionalCaptcha.isPresent() && optionalCaptcha.get().getSecretCode().equals(captcha_secret)) {
                if (userRepository.findByEmail(email).isEmpty()) {
                    User user = new User();
                    user.setIsModerator((byte) 0);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setName("User");
                    user.setEmail(email);
                    userRepository.save(user);
                    log.info("Зарегистрирован пользователь с именем {} ", email);
                    return null;
                }
                errorAnswer.setResult(false);
                errorAnswer.getErrors().put("email", "Этот e-mail уже зарегистрирован");
                return errorAnswer;
            }
            errorAnswer.setResult(false);
            errorAnswer.getErrors().put("captcha", "Код с картинки введён неверно");
            return errorAnswer;
            }
        errorAnswer.setResult(false);
        errorAnswer.getErrors().put("password", "Пароль короче 6-ти символов");
        return errorAnswer;
    }

    public User findById(Integer id)
    {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) log.warn("Пользователь с email {} не найден", user.get().getEmail());
        log.info("Найден пользователь с email {}", user.get().getEmail());
        return user.get();
    }

    @Override
    public AnswerLoginDto login(String email, String password, HttpSession session)
    {
        Optional<User> userOptional = userRepository.findByEmail(email);
        log.info("LOGIN" +userOptional.get().getEmail()
                + " User ");
        User user = userOptional.get();
        String userPassword = user.getPassword();
        log.info("LOGIN " +passwordEncoder.matches(password, userPassword)
                + " НАйДЕН ");
        if(passwordEncoder.matches(password, userPassword) && !providerToken.validateToken(session.getId()))
        {
            providerToken.createToken(session.getId(), user.getId());
            log.info("User with email " + user.getEmail() + " has been authorization");
            Integer moderationCount = userRepository.findCountModerationPostsById(user.getId());
            UserLoginDto answer = new UserLoginDto(user.getId(), user.getName(), user.getPhoto(), user.getEmail(),
                                                   user.getIsModerator(), moderationCount);
            return new AnswerLoginDto(answer);
        }
        log.info("User with email " + user.getEmail() + " IS NOT authorization");
        return null;
    }

    @Override
    public  AnswerLoginDto findBySession(String sessionId){
        Integer userId = providerToken.getUserIdBySession(sessionId);
        if(userId != null){
            Optional<User> userOptional = userRepository.findById(userId);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                log.info("Find user " + user.getEmail());
                Integer moderationCount = userRepository.findCountModerationPostsById(user.getId());
                UserLoginDto answer = new UserLoginDto(user.getId(), user.getName(), user.getPhoto(), user.getEmail(),
                        user.getIsModerator(), moderationCount);
                return new AnswerLoginDto(answer);
            }
        }
        return null;
    }

    public AnswerDto restorePassword(RequestRestoreDto restoreDto){
        Optional<User> optionalUser = userRepository.findByEmail(restoreDto.getEmail());
        if (optionalUser.get() == null) {
            return new AnswerDto(false);
        }
        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        user.setCode(token);
        token = userRepository.save(user).getCode();

        String link = "http://localhost:8004/login/change-password/" + token;
        String message = String.format("Для восстановления пароля перейдите по ссылке %s", link );
        emailService.send(restoreDto.getEmail(), "Password recovery", message);
        return new AnswerDto(true);

    }

    public ErrorAnswerDto setPassword(RequestSetPasswordDto requestDto){
        ErrorAnswerDto errorAnswer = new ErrorAnswerDto();
        if(requestDto.getPassword().length() < 6) {
            codesRepository.deleteAll(codesRepository.findAllOlderCodes(intervalCount));
            Optional<CaptchaCodes> optionalCaptcha = codesRepository.findByCode(requestDto.getCaptcha());
            if (optionalCaptcha.isPresent() && optionalCaptcha.get().getSecretCode().equals(requestDto.getCaptchaSecret())) {
                Optional<User> optionalUser = userRepository.findByCode(requestDto.getCode());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                    userRepository.save(user);
                    return null;
                }
                else errorAnswer.getErrors().put("code", "Ссылка для восстановления пароля устарела.\n" +
                        "<a href=”/auth/restore”>Запросить ссылку снова</a>");
            }
            else errorAnswer.getErrors().put("captcha","Код с картинки введён неверно");
        }
        else errorAnswer.getErrors().put("password", "Пароль короче 6-ти символов");
        return errorAnswer;
    }

    @Override
    public AnswerDto logoutUser(String sessionId) {
        providerToken.deleteToken(sessionId);
        log.info("Удален юзер " + sessionId);
        return new AnswerDto(true);
    }


}
