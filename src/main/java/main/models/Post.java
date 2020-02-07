package main.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @Column(name="is_active", nullable = false)
    private byte isActive;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINE') default 'NEW'")
    private ModerationStatus moderationStatus;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "datetime")
    private Date time;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable=false, updatable=false)
    private User moderatorId = null;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable=false, updatable=false)
    private User user;

    @Getter
    @Setter
    @Column(nullable = false)
    private String title;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Getter
    @Setter
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Getter
    @Setter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TagToPost> seTtags;

    @Getter
    @Setter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  //example
    private Set<PostVotes> setLikesUsers;

}

/*@Getter
    @Setter
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINE') default 'NEW'")
    private ModerationStatus moderationStatus;*/