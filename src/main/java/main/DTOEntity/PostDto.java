package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Date time;

    private UserDto user;

    private String title;

    private String announce;

    private Long likesCount;

    private Long dislikesCount;

    private Integer commentCounts;

    private Integer viewCount;


}
