package main.models;


import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class TagToPost
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id", nullable = false)
    private Integer post;

    @Column(name = "tag_id", nullable = false)
    private Integer tag;

}
