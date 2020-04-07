package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import main.DTOEntity.PostDtoInterface.PostDtoInterface;
import main.model.Post;

import java.time.LocalDateTime;
import java.util.Calendar;

@Data
public class PostModerationDto implements PostDtoInterface
{
    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Calendar time;

    private UserDto user;

    private String title;

    private String announce;

    public PostModerationDto(Post post) {
        this.id = post.getId();
        this.time = post.getTime();
        this.user = new UserDto(post.getUser().getId(), post.getUser().getName());
        this.title = post.getTitle();
        this.announce = post.getText();
    }
}
