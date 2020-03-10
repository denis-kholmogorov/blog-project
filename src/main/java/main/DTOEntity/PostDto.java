package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.DTOEntity.PostDtoInterface.PostDtoInterface;

import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto implements PostDtoInterface {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime time;

    private UserDto user;

    private String title;

    private String announce;

    private Integer likesCount;

    private Integer dislikesCount;

    private Integer commentCounts;

    private Integer viewCount;


}
