package main.services.userService;

import main.DTOEntity.AnswerDto;
import main.DTOEntity.response.ResponseLoginDto;
import main.DTOEntity.AnswerErrorDto;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.model.User;

import javax.servlet.http.HttpSession;

public interface UserService
{
    AnswerErrorDto registerUser(RequestRegisterDto dto);

    User findById(Integer id);

    ResponseLoginDto login(RequestLoginDto dto, HttpSession session);

    ResponseLoginDto findBySession(String sessionId);

    AnswerDto logoutUser (String sessionId);
}
