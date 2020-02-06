package main.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class TagToPost
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "post_id", nullable = false)
    private Integer post;

    @Getter
    @Setter
    @Column(name = "tag_id", nullable = false)
    private Integer tag;
}
