package main.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Setter
    @Getter
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Setter
    @Getter
    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private LocalDateTime time;

    @Setter
    @Getter
    @Column(name = "value", nullable = false, columnDefinition = "tinyint")
    private short value;

}
