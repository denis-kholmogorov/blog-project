package main.DTOEntity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestSetPasswordDto
{
    String code;

    String password;

    String captcha;

    @JsonProperty("captcha_secret")
    String captchaSecret;
}
