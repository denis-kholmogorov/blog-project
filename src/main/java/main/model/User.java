package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Set;

@Data
@ToString(exclude = "")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, columnDefinition = "datetime")
    private Calendar regTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Size(min = 6, message = "password less 6 character")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "code")
    private String code;

    @Column(name = "photo", columnDefinition = "text")
    private String photo;

    @Column(name = "email", nullable = false)
    private String email;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Post> postsSet;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<PostVotes> setLikesPost;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostComments> comments;

}
