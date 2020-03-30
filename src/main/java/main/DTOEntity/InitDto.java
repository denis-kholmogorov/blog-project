package main.DTOEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
public class InitDto
{
    @Value("${spring.copyright.title}")
    private String title;

    @Value("${spring.copyright.subtitle}")
    private String subtitle;

    @Value("${site.copyright.phone}")
    private String phone;

    @Value("${site.copyright.email}")
    private String email;

    @Value("${site.copyright.copyright}")
    private String copyright;

    @Value("${site.copyright.copyrightFrom}")
    private String copyrightFrom;


}
