package main.DTOEntity;

import lombok.Data;

@Data
public class RequestRegisterDto
{
    String e_mail;

    String password;

    String captcha;

    String captcha_secret;
}
