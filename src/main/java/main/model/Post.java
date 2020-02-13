package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;


@Entity
@Table(name = "posts")
public class Post
{
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Getter
    @Column(name="is_active", nullable = false)
    private byte isActive;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINE') default 'NEW'")
    private ModerationStatus moderationStatus;

    @Setter
    @Getter
    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Date time;

    @Setter
    @Getter
    @Column(name = "moderator_id")
    private Integer moderatorId = null;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Getter
    @Column(name = "title",nullable = false)
    private String title;

    @Setter
    @Getter
    @Column(name = "text",nullable = false, columnDefinition = "text")
    private String text;

    @Setter
    @Getter
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Setter
    @Getter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TagToPost> seTtags;

    @Setter
    @Getter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  //example
    private Set<PostVotes> setLikesUsers;

    @Setter
    @Getter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostComments> comments;

}