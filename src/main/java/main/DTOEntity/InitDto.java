package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Data
@Service
@NoArgsConstructor
@AllArgsConstructor
public class InitDto
{
    @Value("${copyright.title}")
    private String title;

    @Value("${copyright.subtitle}")
    private String subtitle;

    @Value("${copyright.phone}")
    private String phone;

    @Value("${copyright.email}")
    private String email;

    @Value("${copyright.copyright}")
    private String copyright;

    @Value("${copyright.copyrightFrom}")
    private String copyrightFrom;

}
