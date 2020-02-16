package main.services.PostServiceInterfaces;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;

public interface PostService
{
    ListPostsDto findAllAndSort(Integer offset, Integer limit, String mode);

    PostDtoId findPostById(Integer id);

    ListPostsDto findAllByDate(Integer offset, Integer limit, String date);

    ListPostsDto findAllByTag(Integer offset, Integer limit, String tag);
}
