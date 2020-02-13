package main.controllers;

import main.DTOEntity.AnswerDto;
import main.DTOEntity.PostDto;
import main.DTOEntity.PostsDto;
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
    public ResponseEntity<AnswerDto> listPost(@RequestParam("offset") int offset,
                                              @RequestParam("limit") int limit,
                                              @RequestParam("mode") String mode){
        Page<Post> posts = null;
        Pageable paging = PageRequest.of(offset, limit);
        Integer countPosts = postRepository.findAllCountPosts();

        if(mode.equals("recent")) {
            posts = postRepository.findAllPostsSortRecent(paging);
        }
        if(mode.equals("popular")){
            List<PostDto> postDtos = postRepository.findAllPostsSortComments(paging).getContent();
            List<PostsDto> postsDtoPostsDto = postDtos.stream().map(this::convertToDTO).collect(Collectors.toList());
            AnswerDto answerDto = new AnswerDto(countPosts, postsDtoPostsDto);
            return ResponseEntity.ok(answerDto);

        }
        if(mode.equals("best")){
            posts = postRepository.findAllPostsSortLikes(paging);
        }
        if(mode.equals("early")){
            posts = postRepository.findAllPostsSortEarly(paging);
        }

       // List<PostDto> postDTOS = posts.toList().stream().map(this::convertToDTO).collect(Collectors.toList());
        return null;//ResponseEntity.ok().body(postDTOS);
    }

    private PostsDto convertToDTO(PostDto post) {
        PostsDto postDto = modelMapper.map(post, PostsDto.class);
        return postDto;
    }

}
