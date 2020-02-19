package main.services.PostService;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;

public interface PostService
{
    ListPostsDto findAllPostsAndSort(Integer offset, Integer limit, String mode);

    PostDtoId findPostById(Integer id);

    ListPostsDto findAllPostsByDate(Integer offset, Integer limit, String date);

    ListPostsDto findAllPostsByTag(Integer offset, Integer limit, String tag);

    ListPostsDto findAllPostsBySearch(Integer offset, Integer limit, String query);
}
