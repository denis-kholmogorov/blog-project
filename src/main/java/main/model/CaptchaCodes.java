package main.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Column(name = "code", nullable = false, columnDefinition = "tinytext")
    private String code;

    @Column(name = "secret_code", nullable = false, columnDefinition = "tinytext")
    private String secretCode;
}
