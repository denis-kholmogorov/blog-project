package main.DTOEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import main.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsDto {
    @Autowired
    ModelMapper modelMapper;
        private Integer id;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
        private Date time;

        private UserDto user;

        private String title;

        private String announce;

        private Long dislikeCount;

        private Long commentCounts;

        private Integer viewCount;

}
