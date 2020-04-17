package main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "tag2post")
public class TagToPost
{
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Getter
    @Column(nullable = false)
    private Integer post_id;

    @Setter
    @Getter
    @Column(nullable = false)
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