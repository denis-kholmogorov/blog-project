package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnswerDto
{
    Integer count;

    List<PostsDto> posts;
}
