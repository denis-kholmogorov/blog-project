package main.model;

import lombok.Data;
import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class Post
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name="is_active", nullable = false)
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINE') default 'NEW'")
    private ModerationStatus moderationStatus;

    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Column(name = "moderator_id")
    private Integer moderatorId = null;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "text",nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TagToPost> seTtags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  //example
    private Set<PostVotes> setLikesUsers;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostComments> comments;

}

/*
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINE') default 'NEW'")
    private ModerationStatus moderationStatus;*/