package main.DTOEntity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestRegisterDto
{
    @JsonProperty(value = "e_mail")
    String email;

    String password;

    String name;

    String captcha;

    @JsonProperty(value = "captcha_secret")
    String captchaSecret;
}
