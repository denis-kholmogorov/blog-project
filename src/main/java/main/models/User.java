package main.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User
{

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "is_moderator", nullable = false)
    private Short isModerator;

    @Getter
    @Setter
    @Column(name = "reg_time", nullable = false, columnDefinition = "datetime")
    private Date regTime;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String photo;

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> postsSet;

    @Getter
    @Setter
    @OneToMany(mappedBy = "moderatorId")
    private Set<Post> moderatorsSet;

    //--------------------------------------------------
}
