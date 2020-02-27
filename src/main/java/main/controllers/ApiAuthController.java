package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.services.captchaService.CaptchaServiceImpl;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController
{
    @Autowired
    CaptchaServiceImpl captchaService;

    @Autowired
    UserServiceImpl userService;

    @Autowired

    @GetMapping("/captcha")
    public ResponseEntity captcha() throws IOException {

        return captchaService.captcha();
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody RequestRegisterDto regDto){

        ErrorAnswerDto answer = userService.registerUser(regDto.getE_mail(),
                                                         regDto.getPassword(),
                                                         regDto.getCaptcha(),
                                                         regDto.getCaptcha_secret());
        if(answer == null) return ResponseEntity.ok().body(new AnswerDto());
        return ResponseEntity.ok().body(answer);
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody RequestLoginDto loginDto)
    {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        AnswerLoginDto answer = userService.findByEmailAndPassword(loginDto.getE_mail(), loginDto.getPassword(), session);
        if(answer == null) {
            return ResponseEntity.ok(new AnswerDto(false));
        }
        return ResponseEntity.ok(answer);
    }

    @GetMapping(value = "/check")
    public ResponseEntity check(HttpSession httpSession){
        String session = httpSession.getId();
        log.info("Name session" + session);
            AnswerLoginDto answer = userService.findBySession(session);
            if (answer == null)
            {
                return ResponseEntity.ok(new AnswerDto(false));
            }
        return ResponseEntity.ok(answer);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity logout(HttpSession session){
        return ResponseEntity.ok(userService.logoutUser(session.getId()));
    }

}
