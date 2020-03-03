package main.services.postService;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.security.ProviderToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl implements PostService {

    PostRepository postRepository;

    ModelMapper modelMapper;

    ProviderToken providerToken;

    @Autowired
    public PostsServiceImpl(PostRepository postRepository, ModelMapper modelMapper, ProviderToken providerToken) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.providerToken = providerToken;
    }



    public ListPostsDto findAllPostsAndSort(Integer offset, Integer limit, String mode)
    {
        Sort sort;
        switch (mode) {
            case "best":
                sort = Sort.by(Sort.Direction.DESC, "likesUsers.size");
                break;
            case "popular":
                sort = Sort.by(Sort.Direction.DESC, "comments.size");
                break;
            case "early":
                sort = Sort.by(Sort.Direction.ASC, "time");
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, "time");
                break;
        }

        Pageable paging = PageRequest.of(offset, limit, sort);

        List<Post> posts = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return listPostsDto;
    }


    public ListPostsDto findAllPostsByDate(Integer offset, Integer limit, String date){
        Pageable paging = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findAllPostsByDate((byte) 1, ModerationStatus.ACCEPTED.toString(), date, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));

        return listPostsDto;
    }

    public ListPostsDto findAllPostsByTag(Integer offset, Integer limit, String tag){
        Pageable paging = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findAllPostsByTag((byte) 1, ModerationStatus.ACCEPTED.toString(), tag, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));

        return listPostsDto;
    }


    public PostDtoId findPostById(Integer id) {
        Optional<Post> post = postRepository.findPostById((byte) 1, ModerationStatus.ACCEPTED, id);
        PostDtoId postDtoId = modelMapper.map(post.get(), PostDtoId.class);
        postDtoId.setTags(post.get().getSetTags().stream().map(t -> t.getName()).collect(Collectors.toSet()));
        postDtoId.setLikesCount(post.get().getLikesUsers().size());
        postDtoId.setDislikesCount(post.get().getDisLikesUsers().size());

        return postDtoId;
    }

    @Override
    public ListPostsDto findAllPostsBySearch(Integer offset, Integer limit, String query) {
        ListPostsDto listPostsDto;

        if(!query.isEmpty()){
            List<Post> posts = postRepository.findPostBySearch((byte) 1, ModerationStatus.ACCEPTED, query);
            listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        }
        else {
            String mode = "popular";
            listPostsDto = findAllPostsAndSort(offset, limit, mode);
        }
        return listPostsDto;
    }

    public ListPostsDto getMyPosts(int offset, int limit, String status, String sessionId){

        if(providerToken.validateToken(sessionId)){

            int userId = providerToken.getUserIdBySession(sessionId);
            Pageable paging = PageRequest.of(offset, limit);
            String query = null;

            switch (status) {
                case "inactive":
                    query = "0";
                    break;
                case "pending":
                    query = "1 and moderationStatus = NEW";
                    break;
                case "declined":
                    query = "1 and moderationStatus = DECLINED";
                    break;
                case "published":
                    query = "1 and moderationStatus = ACCEPTED";
                    break;
            }

            log.info("запрос пользователя к своим записям под id " + userId);
            List<MyPostDto> myPosts = postRepository.findMyPosts(userId, query, paging)
                    .stream()
                    .map(p->new MyPostDto(p))
                    .collect(Collectors.toList());
            log.info("количество записей " + userId + " равно " + myPosts.size());

            return new ListPostsDto(myPosts);
        }
        return null;
    }

    public ListPostsDto getMyModerationPosts(int offset, int limit, String status, String sessionId){

        if(providerToken.validateToken(sessionId)) {
            int userId = providerToken.getUserIdBySession(sessionId);
            Pageable paging = PageRequest.of(offset, limit);
            List<PostModerationDto> posts = postRepository.findMyModerationPosts(userId, status, paging)
                    .stream()
                    .map(p -> new PostModerationDto(p))
                    .collect(Collectors.toList());

            return new ListPostsDto(posts);
        }
        return null;
    }


    private PostDto convertToDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setLikesCount(post.getLikesUsers().size());
        postDto.setDislikesCount(post.getDisLikesUsers().size());
        postDto.setAnnounce(post.getText());
        postDto.setCommentCounts(post.getComments().size());
        return postDto;
    }
}
