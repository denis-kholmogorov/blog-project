package main.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
public class PostVotes
{
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @Getter
    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Setter
    @Getter
    @Column(name = "value", nullable = false, columnDefinition = "tinyint")
    private short value;

}
