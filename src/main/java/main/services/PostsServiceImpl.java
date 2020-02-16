package main.services;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDto;
import main.DTOEntity.PostDtoId;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.services.PostServiceInterfaces.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl implements PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    public ListPostsDto findAllAndSort(Integer offset, Integer limit, String mode)
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

        Pageable pageable = PageRequest.of(offset, limit, sort);

        List<Post> posts = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, pageable);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return listPostsDto;
    }


    public ListPostsDto findAllByDate(Integer offset, Integer limit, String date){
        Pageable paging = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findAllPostsByDate((byte) 1, ModerationStatus.ACCEPTED.toString(), date, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));

        return listPostsDto;
    }

    public ListPostsDto findAllByTag(Integer offset, Integer limit, String tag){
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

    private PostDto convertToDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setLikesCount(post.getLikesUsers().size());
        postDto.setDislikesCount(post.getDisLikesUsers().size());
        postDto.setAnnounce(post.getText());
        postDto.setCommentCounts(post.getComments().size());
        return postDto;
    }
}