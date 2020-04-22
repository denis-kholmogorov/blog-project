package main.services.userService;

import lombok.extern.slf4j.Slf4j;
import main.CustomException.BadRequestException;
import main.CustomException.CustomNotFoundException;
import main.DTOEntity.AnswerDto;
import main.DTOEntity.AnswerErrorDto;
import main.DTOEntity.UserLoginDto;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.DTOEntity.request.RequestRestoreDto;
import main.DTOEntity.request.RequestSetPasswordDto;
import main.DTOEntity.response.ResponseLoginDto;
import main.model.CaptchaCodes;
import main.model.User;
import main.repositories.CaptchaCodesRepository;
import main.repositories.UserRepository;
import main.security.ProviderToken;
import main.security.UserAuthenticationException;
import main.services.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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
    public AnswerErrorDto registerUser(RequestRegisterDto dto) {
        Optional<CaptchaCodes> optionalCaptcha = codesRepository.findByCode(dto.getCaptcha());
        if (dto.getName().matches("[a-zA-ZА-Яа-я]+.?[a-zA-ZА-Яа-я]+") && dto.getName().length() > 2) {
            if (dto.getPassword().length() < 6) throw new BadRequestException("Пароль короче 6-ти символов");
            if (optionalCaptcha.isPresent() && optionalCaptcha.get().getSecretCode().equals(dto.getCaptchaSecret())) {
                if (userRepository.findByEmail(dto.getEmail()).isEmpty()) {
                    User user = new User();
                    user.setIsModerator((byte) 0);
                    user.setPassword(passwordEncoder.encode(dto.getPassword()));
                    user.setName(dto.getName());
                    user.setPhoto("img/default.c66f8640.jpg");
                    user.setEmail(dto.getEmail());
                    userRepository.save(user);
                    return null;
                }
                throw new BadRequestException("Этот e-mail уже зарегистрирован");
            }
            throw new BadRequestException("Код с картинки введён неверно");
        }
        throw new BadRequestException("Имя указано неверно или меньше 3-х символов");
    }

    public User findById(Integer id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) return user.get();
        throw new BadRequestException("User not found");
    }

    @Override
    public ResponseLoginDto login(RequestLoginDto dto, HttpSession session){
        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            String userPassword = user.getPassword();
            if (passwordEncoder.matches(dto.getPassword(), userPassword) && !providerToken.validateToken(session.getId())) {
                providerToken.createToken(session.getId(), user.getId());
                Integer moderationCount = userRepository.findCountModerationPostsById(user.getId());
                UserLoginDto answer = new UserLoginDto(user.getId(), user.getName(), user.getPhoto(), user.getEmail(),
                        user.getIsModerator(), moderationCount);
                return new ResponseLoginDto(answer);
            }
        }
        return null;
    }

    @Override
    public ResponseLoginDto findBySession(String sessionId) throws UserAuthenticationException{
            Integer userId = providerToken.getAuthUserIdBySession(sessionId);
            User user = userRepository.findById(userId).orElseThrow(CustomNotFoundException::new);
            Integer moderationCount = userRepository.findCountModerationPostsById(user.getId());
            UserLoginDto answer = new UserLoginDto(user.getId(), user.getName(), user.getPhoto(), user.getEmail(),
                    user.getIsModerator(), moderationCount);
            return new ResponseLoginDto(answer);
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
        String message = String.format("<a href=\"" + link + "\">Восстановить пароль</a>");
        emailService.send(restoreDto.getEmail(), "Восстановление пароля", message);
        return new AnswerDto(true);
    }

    public AnswerErrorDto setPassword(RequestSetPasswordDto requestDto){
        AnswerErrorDto errorAnswer = new AnswerErrorDto();
        if(requestDto.getPassword().length() < 6) throw new BadRequestException("Пароль короче 6-ти символов");
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
            else throw new BadRequestException("Ссылка для восстановления пароля устарела.\n" +
                    "<a href=”/auth/restore”>Запросить ссылку снова</a>");
        }
        throw new BadRequestException("Код с картинки введён неверно");
    }

    @Override
    public AnswerDto logoutUser(String sessionId) {
        providerToken.deleteToken(sessionId);
        return new AnswerDto(true);
    }
}
