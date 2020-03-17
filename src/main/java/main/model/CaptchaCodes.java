package main.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @CreationTimestamp
    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Calendar time;

    @Column(name = "code", nullable = false, columnDefinition = "tinytext")
    private String code;

    @Column(name = "secret_code", nullable = false, columnDefinition = "tinytext")
    private String secretCode;
}
