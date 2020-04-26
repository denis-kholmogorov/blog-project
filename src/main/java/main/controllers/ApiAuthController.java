package main.controllers;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.DTOEntity.request.RequestRestoreDto;
import main.DTOEntity.request.RequestSetPasswordDto;
import main.DTOEntity.response.ResponseLoginDto;
import main.security.UserAuthenticationException;
import main.services.captchaService.CaptchaServiceImpl;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController
{
    @Autowired
    CaptchaServiceImpl captchaService;

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> captcha() {
        log.info("Сгенерированна каптча");
        return captchaService.captcha();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestRegisterDto regDto){

        AnswerErrorDto answer = userService.registerUser(regDto);
        if(answer == null) {
            log.info("Пользователь зарегистрирован с email" + regDto.getEmail());
            return ResponseEntity.ok().body(new AnswerDto(true));
        }
        log.info("Пользователь не смог зарегистрирован с email" + regDto.getEmail());
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/password")
    public ResponseEntity<?> setPassword(@RequestBody RequestSetPasswordDto requestDto){
        log.info("Восстановление пароля");
        AnswerErrorDto answerDto = userService.setPassword(requestDto);
        if(answerDto == null) return ResponseEntity.ok(new AnswerDto(true));
        return ResponseEntity.ok(answerDto);
    }

    @PostMapping("/restore")
    public ResponseEntity<AnswerDto> restore(@RequestBody RequestRestoreDto restoreDto){
        log.info("Пользователь с email " + restoreDto.getEmail() + " сбрасывает пароль");
        AnswerDto answer =  userService.restorePassword(restoreDto);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody RequestLoginDto loginDto, HttpSession session){
        ResponseLoginDto answer = userService.login(loginDto, session);
        log.info("Пользователь с email " + answer.getUser().getEmail() + " успешно залогинен");
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(HttpSession session){
        String sessionId = session.getId();
        try {
            ResponseLoginDto answer = userService.findBySession(sessionId);
            log.info("Отображены данные пользователя с email " + answer.getUser().getEmail());
            return ResponseEntity.ok(answer);
        }catch (UserAuthenticationException e){
            return ResponseEntity.ok(new AnswerDto(false));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<AnswerDto> logout(HttpSession session){
        return ResponseEntity.ok(userService.logoutUser(session.getId()));
    }


}
