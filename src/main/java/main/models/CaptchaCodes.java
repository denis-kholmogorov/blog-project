package main.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Getter
    @Setter
    @Column(name = "code", nullable = false, columnDefinition = "tinytext")
    private String code;

    @Getter
    @Setter
    @Column(name = "secret_code", nullable = false, columnDefinition = "tinytext")
    private String secretCode;
}
