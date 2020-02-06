package main.models;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="is_active", nullable = false)
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, columnDefinition = "varchar(255) default 'NEW'")
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User moderatorId = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable=false, updatable=false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

}
