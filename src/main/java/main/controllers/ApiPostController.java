package main.controllers;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDto;
import main.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/post")
public class ApiPostController
{

    @Autowired
    PostsService postsService;

    @GetMapping(params = {"offset", "limit", "mode"})
    public ResponseEntity<ListPostsDto> listPost(@RequestParam("offset") int offset,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("mode") String mode)
    {
        List<PostDto> postsDtoPostsDto = postsService.findAllAndSort(offset, limit, mode);
        return ResponseEntity.ok(new ListPostsDto(postsDtoPostsDto.size(), postsDtoPostsDto));
    }

    @GetMapping(value = "/byDate", params = {"offset", "limit", "date"})
    public ResponseEntity<ListPostsDto> listPostByDate(@RequestParam("offset") int offset,
                                                       @RequestParam("limit") int limit,
                                                       @RequestParam("date") String date)
    {
        List<PostDto> postsDtoPostsDto = postsService.findAllByDate(offset, limit, date);
        return ResponseEntity.ok(new ListPostsDto(postsDtoPostsDto.size(), postsDtoPostsDto));
    }

    @GetMapping(value = "/byTag", params = {"offset", "limit", "tag"})
    public ResponseEntity<ListPostsDto> listPostByTag(@RequestParam("offset") int offset,
                                                      @RequestParam("limit") int limit,
                                                      @RequestParam("tag") String tag)
    {


        List<PostDto> postsDtoPostsDto = postsService.findAllByTag(offset, limit, tag);
        return ResponseEntity.ok(new ListPostsDto(postsDtoPostsDto.size(), postsDtoPostsDto));
    }



}
