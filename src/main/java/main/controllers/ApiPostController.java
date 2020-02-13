package main.controllers;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDto;
import main.model.Post;
import main.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/post")
public class ApiPostController
{

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping(params = {"offset", "limit", "mode"})
    public ResponseEntity<ListPostsDto> listPost(@RequestParam("offset") int offset,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("mode") String mode){
        Page<Post> posts = null;
        Pageable paging = PageRequest.of(offset, limit);

        if(mode.equals("recent")) {
            posts = postRepository.findAllPostsSortRecent(paging);
        }
        if(mode.equals("popular")){
            posts = postRepository.findAllPostsSortComments(paging);
        }
        if(mode.equals("best")){
            posts = postRepository.findAllPostsSortLikes(paging);
        }
        if(mode.equals("early")){
            posts = postRepository.findAllPostsSortEarly(paging);
        }

        List<PostDto> postsDtoPostsDto = posts.toList().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ListPostsDto(postsDtoPostsDto.size(), postsDtoPostsDto));
    }

    private PostDto convertToDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setLikesCount(post.getSetLikesUsers().stream().filter(l->{return l.getValue() == 1;}).count());
        postDto.setDislikesCount(post.getSetLikesUsers().stream().filter(l->{return l.getValue() == -1;}).count());
        postDto.setAnnounce(post.getText());
        postDto.setCommentCounts(post.getComments().size());
        return postDto;
    }

}
