package main.services.userService;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.ErrorAnswerDto;
import main.model.CaptchaCodes;
import main.model.User;
import main.repositories.CaptchaCodesRepository;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl
{

    UserRepository userRepository;

    CaptchaCodesRepository codesRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CaptchaCodesRepository codesRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.codesRepository = codesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ErrorAnswerDto registerUser(String email, String name, String password, String captcha, String captcha_secret) {
        ErrorAnswerDto errorAnswer = new ErrorAnswerDto();
        List<CaptchaCodes> captchaList = codesRepository.findAllByCode(captcha);
        if (password.length() >= 6) {
            if (name.matches("[A-Za-zА-Яа-я-]+")) {
                if (captchaList.size() == 1 && captchaList.get(0).getSecretCode().equals(captcha_secret)) {
                    if (userRepository.findByEmail(email).isEmpty()) {
                        User user = new User();
                        user.setIsModerator((byte) 0);
                        user.setName(name);
                        user.setPassword(passwordEncoder.encode(password));
                        user.setEmail(email);
                        userRepository.save(user);
                        log.info("Зарегистрирован пользователь с именем {} ", name);
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
            errorAnswer.getErrors().put("name", "Имя указано неверно");
            return errorAnswer;
        }
        errorAnswer.setResult(false);
        errorAnswer.getErrors().put("password", "Пароль короче 6-ти символов");
        return errorAnswer;
    }

    public User findByUserEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) log.warn("Пользователь с email {} не найден", email );
        log.info("Найден пользователь с email {}", email );
        return user.get();
    }
}
