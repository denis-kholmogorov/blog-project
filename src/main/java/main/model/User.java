package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.sql.Date;
import java.util.Set;


@Entity
@Table(name = "users")
public class User {

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Getter
    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;

    @Setter
    @Getter
    @Column(name = "reg_time", nullable = false, columnDefinition = "datetime")
    private Date regTime;

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Getter
    @Column(name = "code")
    private String code;

    @Setter
    @Getter
    @Column(name = "photo", columnDefinition = "text")
    private String photo;

    @Setter
    @Getter
    @Column(name = "email", nullable = false)
    private String email;

    @Setter
    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Post> postsSet;

    @Setter
    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostVotes> setLikesPost;

    @Setter
    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostComments> comments;

}
