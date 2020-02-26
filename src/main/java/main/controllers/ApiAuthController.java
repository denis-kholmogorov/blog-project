package main.controllers;

import main.DTOEntity.AccessAnswerDto;
import main.DTOEntity.ErrorAnswerDto;
import main.services.captchaService.CaptchaServiceImpl;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping(value = "/register", params = {"e_mail", "name", "password", "captcha","captcha_secret"})
    public ResponseEntity register(@RequestParam("e_mail") String email,
                                              @RequestParam("name") String name,
                                              @RequestParam("password") String password,
                                              @RequestParam("captcha") String captcha,
                                              @RequestParam("captcha_secret") String captcha_secret){

        ErrorAnswerDto answer = userService.registerUser(email,name, password, captcha,captcha_secret);

        if(answer == null) return ResponseEntity.ok().body(new AccessAnswerDto());

        return ResponseEntity.ok().body(answer);
    }
}
