package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "tag2post")
public class TagToPost
{
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Getter
    private Integer post_id;

    @Setter
    @Getter
    private Integer tag_id;
}

/*@Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;*/