package main.services.postService;

import main.DTOEntity.AnswerDto;
import main.DTOEntity.LikeRequestDto;
import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;
import main.DTOEntity.request.RequestPostDto;

import javax.servlet.http.HttpSession;
import java.text.ParseException;

public interface PostService
{
    ListPostsDto findAllPostsAndSort(Integer offset, Integer limit, String mode);

    PostDtoId findPostById(Integer id, HttpSession session);

    ListPostsDto findAllPostsByDate(Integer offset, Integer limit, String date);

    ListPostsDto findAllPostsByTag(Integer offset, Integer limit, String tag);

    ListPostsDto findAllPostsBySearch(Integer offset, Integer limit, String query);

    ListPostsDto getMyPosts(int offset, int limit, String status, String sessionId);

    ListPostsDto getMyModerationPosts(int offset, int limit, String status, String sessionId);

    AnswerDto createPost(RequestPostDto postDto, String session) throws ParseException;

    AnswerDtoInterface changePost(Integer id, RequestPostDto postDto, String session);

    AnswerDto setLikePost(LikeRequestDto likeDto, HttpSession session);

    AnswerDto setDislikePost(LikeRequestDto likeDto, HttpSession session);
}
