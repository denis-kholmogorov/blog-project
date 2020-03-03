package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.DTOEntity.PostDtoInterface.PostDtoInterface;
import main.model.Post;

import java.util.Calendar;

@Data
@NoArgsConstructor
public class MyPostDto implements PostDtoInterface {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Calendar time;

    private String title;

    private String announce;

    private Integer likesCount;

    private Integer dislikesCount;

    private Integer commentCounts;

    private Integer viewCount;

    public MyPostDto(Post post) {
        this.id = post.getId();
        this.time = post.getTime();
        this.title = post.getTitle();
        this.announce = post.getText();
        this.likesCount = post.getLikesUsers().size();
        this.dislikesCount = post.getDisLikesUsers().size();
        this.commentCounts = post.getComments().size();
        this.viewCount = post.getViewCount();
    }
}
