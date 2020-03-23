package main.DTOEntity;

import lombok.Data;
import main.DTOEntity.PostDtoInterface.PostDtoInterface;

import java.util.List;

@Data
public class ListPostsDto
{
    Integer count;

    List<PostDtoInterface> posts;

    public ListPostsDto(List posts){
        this.posts = posts;
    }
}
