package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Calendar;
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
    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, columnDefinition = "datetime")
    private Calendar regTime;

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Getter
    @Size(min = 6, message = "password less 6 character")
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
