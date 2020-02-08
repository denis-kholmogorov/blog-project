package main.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
public class PostVotes
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Column(name = "value", nullable = false, columnDefinition = "tinyint")
    private short value;

}
