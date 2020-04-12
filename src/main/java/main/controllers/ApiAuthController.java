package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.DTOEntity.request.RequestRestoreDto;
import main.DTOEntity.request.RequestSetPasswordDto;
import main.DTOEntity.response.ResponseLoginDto;
import main.services.captchaService.CaptchaServiceImpl;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestRegisterDto regDto){
        AnswerErrorDto answer = userService.registerUser(regDto);
        if(answer == null) return ResponseEntity.ok().body(new AnswerDto(true));
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/password")
    public ResponseEntity<?> setPassword(@RequestBody RequestSetPasswordDto requestDto){
        AnswerErrorDto answerDto = userService.setPassword(requestDto);
        if(answerDto == null) return ResponseEntity.ok(new AnswerDto(true));
        return ResponseEntity.ok(answerDto);
    }

    @PostMapping("/restore")
    public ResponseEntity<AnswerDto> restore(@RequestBody RequestRestoreDto restoreDto){
        AnswerDto answer =  userService.restorePassword(restoreDto);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto loginDto, HttpSession session){
        ResponseLoginDto answer = userService.login(loginDto, session);
        return ResponseEntity.ok(Objects.requireNonNullElseGet(answer, () -> new AnswerDto(false)));
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(HttpSession session){
        String sessionId = session.getId();
        ResponseLoginDto answer = userService.findBySession(sessionId);
        if (answer == null) return ResponseEntity.ok(new AnswerDto(false));
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/logout")
    public ResponseEntity<AnswerDto> logout(HttpSession session){
        return ResponseEntity.ok(userService.logoutUser(session.getId()));
    }


}
