package main.controllers;

import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;

import main.services.postService.PostsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/post")
public class ApiPostController
{

    @Autowired
    PostsServiceImpl postsServiceImpl;

    @GetMapping(params = {"offset", "limit", "mode"})
    public ResponseEntity<ListPostsDto> listPost(@RequestParam("offset") int offset,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("mode") String mode)
    {
        ListPostsDto listPostsDto = postsServiceImpl.findAllPostsAndSort(offset, limit, mode);

        return ResponseEntity.ok(listPostsDto);
    }

    @GetMapping(value = "/byDate", params = {"offset", "limit", "date"})
    public ResponseEntity<ListPostsDto> listPostByDate(@RequestParam("offset") int offset,
                                                       @RequestParam("limit") int limit,
                                                       @RequestParam("date") String date)
    {
        ListPostsDto listPostsDto = postsServiceImpl.findAllPostsByDate(offset, limit, date);
        return ResponseEntity.ok(listPostsDto);
    }

    @GetMapping(value = "/byTag", params = {"offset", "limit", "tag"})
    public ResponseEntity<ListPostsDto> listPostByTag(@RequestParam("offset") int offset,
                                                      @RequestParam("limit") int limit,
                                                      @RequestParam("tag") String tag)
    {
        ListPostsDto listPostsDto = postsServiceImpl.findAllPostsByTag(offset, limit, tag);
        return ResponseEntity.ok(listPostsDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostDtoId> postById(@PathVariable("id") Integer id)
    {
        PostDtoId post = postsServiceImpl.findPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping(value ="/search", params = {"offset", "limit", "query"})
    public ResponseEntity<ListPostsDto> postBySearch(@RequestParam("offset") int offset,
                                                     @RequestParam("limit") int limit,
                                                     @RequestParam("query") String query)
    {
        ListPostsDto listPostsDto = postsServiceImpl.findAllPostsBySearch(offset, limit, query);
        return ResponseEntity.ok(listPostsDto);
    }
}
