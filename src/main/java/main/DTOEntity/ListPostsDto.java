package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListPostsDto
{
    Integer count;

    List<PostDto> posts;
}
