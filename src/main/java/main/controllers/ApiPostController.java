package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.ListPostsDto;
import main.DTOEntity.PostDtoId;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;
import main.DTOEntity.PostRequestDto;
import main.security.ProviderToken;
import main.services.postService.PostsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/post")
public class ApiPostController
{
    PostsServiceImpl postsServiceImpl;

    ProviderToken providerToken;

    @Autowired
    public ApiPostController(PostsServiceImpl postsServiceImpl, ProviderToken providerToken) {
        this.postsServiceImpl = postsServiceImpl;
        this.providerToken = providerToken;
    }

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
    public ResponseEntity<ListPostsDto> getPostsBySearch(@RequestParam("offset") int offset,
                                                         @RequestParam("limit") int limit,
                                                         @RequestParam("query") String query)
    {
        ListPostsDto listPostsDto = postsServiceImpl.findAllPostsBySearch(offset, limit, query);
        return ResponseEntity.ok(listPostsDto);
    }

    @GetMapping(value = "/my", params = {"offset","limit","status"})
    public ResponseEntity getMyPosts(@RequestParam("offset") int offset,
                                     @RequestParam("limit") int limit,
                                     @RequestParam("status") String status,
                                     HttpSession httpSession){
        ListPostsDto myPosts = postsServiceImpl.getMyPosts(offset,limit,status,httpSession.getId());
        if(myPosts != null) return ResponseEntity.ok(myPosts);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping(value = "/moderation", params ={"offset","limit","status"})
    public ResponseEntity getMyModerationPosts(@RequestParam("offset") int offset,
                                               @RequestParam("limit") int limit,
                                               @RequestParam("status") String status,
                                               HttpSession httpSession)
    {
        ListPostsDto listPostsDto = postsServiceImpl.getMyModerationPosts(offset, limit, status, httpSession.getId());
        if(listPostsDto != null) return ResponseEntity.ok(listPostsDto);
        return null;
    }

    @PostMapping()
    public ResponseEntity createPost(@RequestBody PostRequestDto post, HttpSession session) throws ParseException {

        AnswerDtoInterface answer = postsServiceImpl.createPost(post, session.getId());
        if(answer != null){
            return ResponseEntity.ok(answer);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


}
