package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AnswerLoginDto
{
    boolean result = true;

    public AnswerLoginDto(UserLoginDto user) {
        this.user = user;
    }

    UserLoginDto user;
}
