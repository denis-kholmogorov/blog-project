package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestRegisterDto
{
    @JsonProperty(value = "e_mail")
    String email;

    String password;

    String captcha;

    String captcha_secret;
}
