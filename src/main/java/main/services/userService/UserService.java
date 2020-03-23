package main.services.userService;

import main.DTOEntity.AnswerDto;
import main.DTOEntity.AnswerLoginDto;
import main.DTOEntity.ErrorAnswerDto;
import main.model.User;

import javax.servlet.http.HttpSession;

public interface UserService
{
    ErrorAnswerDto registerUser(String email, String password, String captcha, String captcha_secret);

    User findById(Integer id);

    AnswerLoginDto login(String email, String password, HttpSession session);

    AnswerLoginDto findBySession(String sessionId);

    AnswerDto logoutUser (String sessionId);
}
