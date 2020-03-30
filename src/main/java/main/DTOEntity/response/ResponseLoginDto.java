package main.DTOEntity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.DTOEntity.UserLoginDto;

@Data
public class ResponseLoginDto
{
    boolean result = true;

    UserLoginDto user;

    public ResponseLoginDto(UserLoginDto user) {
        this.user = user;
    }
}
