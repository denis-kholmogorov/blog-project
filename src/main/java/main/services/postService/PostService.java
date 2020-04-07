package main.services.postService;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;

import javax.servlet.http.HttpSession;

public interface PostService
{
    ListPostsDto findAllPostsAndSort(Integer offset, Integer limit, String mode);

    PostDtoId findPostById(Integer id, HttpSession session);

    ListPostsDto findAllPostsByDate(Integer offset, Integer limit, String date);

    ListPostsDto findAllPostsByTag(Integer offset, Integer limit, String tag);

    ListPostsDto findAllPostsBySearch(Integer offset, Integer limit, String query);
}
