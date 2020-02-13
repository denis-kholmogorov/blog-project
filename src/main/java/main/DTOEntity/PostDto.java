package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import main.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@NoArgsConstructor
public class PostDto {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Date time;

    private User user;

    private String title;

    private String announce;

    private Long likesCount;

    private Long dislikesCount;

    private Long commentCounts;

    private Integer viewCount;

    /*public PostDto(int id, java.util.Date time, User user, String title, String announce, long dislikeCount, long commentCounts, int viewCount) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.title = title;
        this.announce = announce;
        this.dislikeCount = dislikeCount;
        this.commentCounts = commentCounts;
        this.viewCount = viewCount;
    }*/
    public PostDto(Post post, long commentCounts) {
        this.id = post.getId();
        this.time = post.getTime();
        this.user = post.getUser();
        this.title = post.getTitle();
        this.announce = post.getText();
        this.dislikesCount = post.getSetLikesUsers().stream().filter(postVotes -> {
            return postVotes.getValue() == -1;
        }).count();
        this.likesCount = post.getSetLikesUsers().stream().filter(postVotes -> {
            return postVotes.getValue() == 1;
        }).count();
        this.commentCounts = commentCounts;
        this.viewCount = post.getViewCount();
    }

}
