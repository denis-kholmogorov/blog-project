package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaDto
{
    String secret;

    String image;
}
