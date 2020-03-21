package main.services.userService;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.AnswerDto;
import main.DTOEntity.AnswerLoginDto;
import main.DTOEntity.ErrorAnswerDto;
import main.DTOEntity.UserLoginDto;
import main.model.CaptchaCodes;
import main.model.User;
import main.repositories.CaptchaCodesRepository;
import main.repositories.UserRepository;
import main.security.ProviderToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{

    private UserRepository userRepository;

    private CaptchaCodesRepository codesRepository;

    private PasswordEncoder passwordEncoder;

    private ProviderToken providerToken;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CaptchaCodesRepository codesRepository,
                           PasswordEncoder passwordEncoder,
                           ProviderToken providerToken) {
        this.userRepository = userRepository;
        this.codesRepository = codesRepository;
        this.passwordEncoder = passwordEncoder;
        this.providerToken = providerToken;
    }

    @Override
    public ErrorAnswerDto registerUser(String email, String password, String captcha, String captcha_secret) {
        ErrorAnswerDto errorAnswer = new ErrorAnswerDto();
        List<CaptchaCodes> captchaList = codesRepository.findAllByCode(captcha);
        if (password.length() >= 6) {
            if (captchaList.size() == 1 && captchaList.get(0).getSecretCode().equals(captcha_secret)) {
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
        log.info("LOGIN" +passwordEncoder.matches(password, userPassword)
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

    @Override
    public AnswerDto logoutUser(String sessionId) {
        providerToken.deleteToken(sessionId);
        log.info("Удален юзер " + sessionId);
        return new AnswerDto(true);
    }


}
