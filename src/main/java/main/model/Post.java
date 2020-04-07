package main.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@ToString(exclude = {"likesUsers","disLikesUsers","comments"})
@EqualsAndHashCode(exclude = "setTags")
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
    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED') default 'NEW'")
    private ModerationStatus moderationStatus;

    @Column(name = "time", nullable = false, columnDefinition = "datetime")
    private Calendar time;

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

    @ManyToMany
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> setTags = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)   //example
    @JoinColumn(name = "post_id")
    @Where(clause = "value = 1")
    private List<PostVotes> likesUsers = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)  //example
    @JoinColumn(name = "post_id")
    @Where(clause = "value = -1")
    private List<PostVotes> disLikesUsers = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComments> comments = new ArrayList<>();;

}

    /*@Setter
    @Getter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TagToPost> setTags;*/