package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data

public class ListPostsDto
{
    Integer count;

    List<PostDto> posts;

    public ListPostsDto(List posts){
        this.posts = posts;
        this.count = posts.size();
    }
}
