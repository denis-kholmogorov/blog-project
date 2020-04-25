package main.DTOEntity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
