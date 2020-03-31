package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.DTOEntity.PostDtoInterface.PostDtoInterface;
import main.model.Post;
import main.model.User;

import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@NoArgsConstructor
public class MyPostDto implements PostDtoInterface {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime time;

    private User user;

    private String title;

    private String announce;

    private Integer likeCount;

    private Integer dislikeCount;

    private Integer commentCount;

    private Integer viewCount;

    public MyPostDto(Post post) {
        this.id = post.getId();
        this.time = post.getTime();
        this.title = post.getTitle();
        this.user = post.getUser();
        this.announce = post.getText();
        this.likeCount = post.getLikesUsers().size();
        this.dislikeCount = post.getDisLikesUsers().size();
        this.commentCount = post.getComments().size();
        this.viewCount = post.getViewCount();
    }
}
