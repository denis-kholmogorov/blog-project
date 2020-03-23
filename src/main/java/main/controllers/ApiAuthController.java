package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.DTOEntity.request.RequestRestoreDto;
import main.DTOEntity.request.RequestSetPasswordDto;
import main.services.captchaService.CaptchaServiceImpl;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
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

        return captchaService.captcha();
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody RequestRegisterDto regDto){

        ErrorAnswerDto answer = userService.registerUser(regDto.getEmail(),
                                                         regDto.getPassword(),
                                                         regDto.getCaptcha(),
                                                         regDto.getCaptcha_secret());
        if(answer == null) return ResponseEntity.ok().body(new AnswerDto());
        return ResponseEntity.ok(answer);
    }

    @PostMapping(value = "/password")
    public ResponseEntity<?> setPassword(@RequestBody RequestSetPasswordDto requestDto)
    {
        ErrorAnswerDto answerDto = userService.setPassword(requestDto);
        if(answerDto == null) return ResponseEntity.ok(new AnswerDto(true));
        return ResponseEntity.ok(answerDto);
    }


    @PostMapping(value = "/restore")
    public ResponseEntity<AnswerDto> restore(@RequestBody RequestRestoreDto restoreDto){
        log.info("Restore email " + restoreDto);
        AnswerDto answer =  userService.restorePassword(restoreDto);
        return ResponseEntity.ok(answer);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto loginDto, HttpSession session)
    {
        AnswerLoginDto answer = userService.login(loginDto.getE_mail(), loginDto.getPassword(), session);
        return ResponseEntity.ok(Objects.requireNonNullElseGet(answer, () -> new AnswerDto(false)));
    }

    @GetMapping(value = "/check")
    public ResponseEntity<?> check(HttpSession httpSession){
        String session = httpSession.getId();
        log.info("Name session" + session);
        AnswerLoginDto answer = userService.findBySession(session);

            if (answer == null)
            {
                return ResponseEntity.ok(new AnswerDto(false));
            }

        log.info("Пользователь зарегистрирован");
        return ResponseEntity.ok(answer);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<AnswerDto> logout(HttpSession session){
        return ResponseEntity.ok(userService.logoutUser(session.getId()));
    }


}
