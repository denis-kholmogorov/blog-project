package main.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name="global_settings")
public class GlobalSettings
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;


}
