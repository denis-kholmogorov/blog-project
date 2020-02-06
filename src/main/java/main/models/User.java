package main.models;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_moderator", nullable = false)
    private Short isModerator;

    @Column(name = "reg_time", nullable = false, columnDefinition = "datetime")
    private Date regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String code;

    private String photo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> postsSet;

    @OneToMany(mappedBy = "moderatorId")
    private Set<Post> moderatorsSet;

    //--------------------------------------------------

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Short isModerator) {
        this.isModerator = isModerator;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


}