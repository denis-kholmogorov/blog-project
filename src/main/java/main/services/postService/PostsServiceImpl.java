package main.services.postService;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;
import main.model.*;
import main.repositories.*;
import main.security.ProviderToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl implements PostService {

    PostRepository postRepository;

    ModelMapper modelMapper;

    ProviderToken providerToken;

    UserRepository userRepository;

    TagRepository tagRepository;

    TagToPostRepository tagToPostRepository;

    PostVotesRepository postVotesRepository;

    @Autowired
    public PostsServiceImpl(PostRepository postRepository,
                            ModelMapper modelMapper,
                            ProviderToken providerToken,
                            UserRepository userRepository,
                            TagRepository tagRepository,
                            TagToPostRepository tagToPostRepository,
                            PostVotesRepository postVotesRepository) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.providerToken = providerToken;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.postVotesRepository = postVotesRepository;
    }


    public ListPostsDto findAllPostsAndSort(Integer offset, Integer limit, String mode)
    {
        Sort sort;
        switch (mode) {
            case "best":
                sort = Sort.by("likesUsers.size").descending();
                break;
            case "popular":
                sort = Sort.by("comments.size").descending();
                break;
            case "early":
                sort = Sort.by("time").ascending();
                break;
            default:
                sort = Sort.by("time").descending();
                break;
        }

        Pageable paging = PageRequest.of((offset/limit), limit, sort);

        Page<Post> posts = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        listPostsDto.setCount((int)posts.getTotalElements());
        return listPostsDto;
    }


    public ListPostsDto findAllPostsByDate(Integer offset, Integer limit, String date){
        Pageable paging = PageRequest.of((offset/limit), limit);
        Page<Post> posts = postRepository.findAllPostsByDate((byte) 1, ModerationStatus.ACCEPTED.toString(), date, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        listPostsDto.setCount((int) posts.getTotalElements());
        return listPostsDto;
    }

    public ListPostsDto findAllPostsByTag(Integer offset, Integer limit, String tag){
        Pageable paging = PageRequest.of((offset/limit), limit);
        Page<Post> posts = postRepository.findAllPostsByTag((byte) 1, ModerationStatus.ACCEPTED.toString(), tag, paging);
        ListPostsDto listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
        listPostsDto.setCount((int) posts.getTotalElements());
        return listPostsDto;
    }


    public PostDtoId findPostById(Integer id) {
        log.info(" запрос поста " + id);
        Post post = postRepository.findPostById((byte) 1, ModerationStatus.ACCEPTED, id).orElse(null);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(post != null && post.getTime().toLocalTime().isBefore(LocalDateTime.now().toLocalTime().plusMinutes(1))){
            log.info(post.getId() + " id поста");
            post.getViewCount();
            PostDtoId postDtoId = modelMapper.map(post, PostDtoId.class);
            postDtoId.setTags(post.getSetTags().stream().map(t -> t.getName()).collect(Collectors.toSet()));
            postDtoId.setLikeCount(post.getLikesUsers().size());
            postDtoId.setDislikeCount(post.getDisLikesUsers().size());
            return postDtoId;
        }
        log.warn("пост не извлечен");
        return null;

    }

    @Override
    public ListPostsDto findAllPostsBySearch(Integer offset, Integer limit, String query) {
        ListPostsDto listPostsDto;

        if(!query.isEmpty()){
            Page<Post> posts = postRepository.findPostBySearch((byte) 1,
                                                               ModerationStatus.ACCEPTED,
                                                               query,
                                                               PageRequest.of((offset/limit), limit));
            listPostsDto = new ListPostsDto(posts.stream().map(this::convertToDTO).collect(Collectors.toList()));
            listPostsDto.setCount((int)posts.getTotalElements());
        }
        else {
            String mode = "popular";
            listPostsDto = findAllPostsAndSort((offset/limit), limit, mode);
        }
        return listPostsDto;
    }

    public ListPostsDto getMyPosts(int offset, int limit, String status, String sessionId){

        if(providerToken.validateToken(sessionId)){
            int userId = providerToken.getUserIdBySession(sessionId);
            Pageable paging = PageRequest.of((offset/limit), limit);
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
            Page<Post> page = postRepository.findMyPosts(userId, query, paging);
            List<MyPostDto> myPosts = page
                    .stream()
                    .map(p->new MyPostDto(p))
                    .collect(Collectors.toList());
            log.info("количество записей " + userId + " равно " + myPosts.size());
            ListPostsDto listPostsDto = new ListPostsDto(myPosts);
            listPostsDto.setCount((int)page.getTotalElements());
            return listPostsDto;
        }
        return null;
    }

    public ListPostsDto getMyModerationPosts(int offset, int limit, String status, String sessionId){

        if(providerToken.validateToken(sessionId)) {
            int userId = providerToken.getUserIdBySession(sessionId);
            Pageable paging = PageRequest.of((offset/limit), limit, Sort.by("time").descending());
            List<PostModerationDto> postsList;
            Page<Post> posts;
            if(status.equals("new")) {
                posts = postRepository.findModerationNewPosts(paging);
                postsList = posts.stream()
                        .map(p -> new PostModerationDto(p))
                        .collect(Collectors.toList());
            }else{
                posts = postRepository.findMyModerationPosts(userId, status, paging);
                postsList = posts.stream()
                        .map(p -> new PostModerationDto(p))
                        .collect(Collectors.toList());

            }
            ListPostsDto listPostsDto = new ListPostsDto(postsList);
            listPostsDto.setCount((int)posts.getTotalElements());
            return listPostsDto;
        }
        return null;
    }

    public AnswerDtoInterface createPost(RequestPostDto postDto, String session){
        if(providerToken.validateToken(session)){
            LocalDateTime time = LocalDateTime.parse(postDto.getTime().replace("T"," "),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            log.info(time.toLocalTime().isBefore(LocalDateTime.now().toLocalTime())+" время установленное постом");
            if(time.toLocalTime().isBefore(LocalDateTime.now().plusMinutes(1).toLocalTime())) {
                time = LocalDateTime.now();
            }
            Map<String, String> error = new HashMap<>();
            if(postDto.getTitle().length() >= 10){
                if(postDto.getText().length() >= 5){
                    Integer userId = providerToken.getUserIdBySession(session);
                    Post post = new Post();
                    post.setUser(userRepository.findById(userId).get());
                    post.setIsActive(postDto.getActive());
                    log.info(postDto.getActive() + " активность поста");
                    post.setTime(time);
                    post.setText(postDto.getText());
                    post.setTitle(postDto.getTitle());
                    post.setViewCount(0);
                    post.setModerationStatus(ModerationStatus.NEW);
                    Post p = postRepository.save(post);
                    log.info(p.getId() + " id сохраняемого поста");
                    postDto.getTags().forEach(t ->{
                        Tag tag = tagRepository.findByName(t).orElse(null);
                        if(tag != null) {
                            TagToPost tp = new TagToPost();
                            tp.setTag_id(tag.getId());
                            tp.setPost_id(post.getId());
                            tp.setId((int) tagToPostRepository.count() + 1);
                            tagToPostRepository.save(tp);
                            log.info(tag.getName());
                        }
                        else {log.info("Tag not fount");}
                    });

                    return new AnswerDto(true);
                }
                else {
                    error.put("text", "Текст публикации слишком короткий");
                }
            }else {
                error.put("title", "Заголовок слишком короткий или его нет");
            }
            return new ErrorAnswerDto(false, error);
        }
        return null;
    }

    public AnswerDtoInterface changePost(Integer id, RequestPostDto postDto, String session){
        if(providerToken.validateToken(session)) {
            log.info(postDto.getTime());
            Post post = postRepository.findById(id).get();
            LocalDateTime time = LocalDateTime.parse(postDto.getTime().replace("T", " "),
                    DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm"));

            log.info(time.toLocalTime().isAfter(LocalDateTime.now().toLocalTime()) + " время установленное постом");
            if (time.toLocalTime().isBefore(LocalDateTime.now().minusMinutes(1).toLocalTime())) {
                time = LocalDateTime.now();
            }
            Map<String, String> error = new HashMap<>();
            if (postDto.getTitle().length() >= 10) {
                if (postDto.getText().length() >= 5) {
                    post.setTime(time);
                    post.setIsActive(postDto.getActive());
                    post.setTitle(postDto.getTitle());
                    post.setText(postDto.getText());
                    postRepository.save(post);
                    postDto.getTags().forEach(t ->{
                        Tag tag = tagRepository.findByName(t).orElse(null);
                        if(tag != null && !post.getSetTags().contains(tag)) {
                            TagToPost tp = new TagToPost();
                            tp.setTag_id(tag.getId());
                            tp.setPost_id(post.getId());
                            tp.setId((int) tagToPostRepository.count() + 1);
                            tagToPostRepository.save(tp);
                            log.info(tag.getName());
                        }
                        else {log.info("Tag not fount");}
                    });
                    return new AnswerDto(true);
                }
                else {
                    error.put("text", "Текст публикации слишком короткий");
                }
            }
            else {
                error.put("title", "Заголовок слишком короткий или его нет");
            }
            return new ErrorAnswerDto(false, error);
        }
        return null;
    }

    public AnswerDto setLikePost(LikeRequestDto likeDto, HttpSession session) {
        Post post = postRepository.findById(likeDto.getPostId()).get();
        Integer userId = providerToken.getUserIdBySession(session.getId());
        PostVotes votes = postVotesRepository.findByPostIdAndUserId(likeDto.getPostId(), userId).orElse(null);
        if (votes == null) {
            PostVotes postVotes = PostVotes.builder()
                    .postId(likeDto.getPostId())
                    .userId(userId)
                    .time(LocalDateTime.now())
                    .value((short) 1)
                    .build();
            log.info("Like сохранен от пользователя " + userId + " пост " + likeDto.getPostId());
            postVotesRepository.save(postVotes);
            post.getLikesUsers().add(postVotes);
            return new AnswerDto(true);
        }

        if (votes.getValue() == (short) 1) {
            return new AnswerDto(false);
        } else {
            votes.setValue((short) 1);
            postVotesRepository.save(votes);
            return new AnswerDto(true);
        }
    }
        public AnswerDto setDislikePost(LikeRequestDto likeDto, HttpSession session){
            Integer userId = providerToken.getUserIdBySession(session.getId());
            PostVotes votes = postVotesRepository.findByPostIdAndUserId(likeDto.getPostId(), userId).orElse(null);
            Post post = postRepository.findById(likeDto.getPostId()).get();
            if (votes == null) {
                log.info(LocalDateTime.now() + "");
                PostVotes postVotes = PostVotes.builder()
                        .postId(likeDto.getPostId())
                        .userId(userId)
                        .time(LocalDateTime.now())
                        .value((short) -1)
                        .build();
                postVotesRepository.save(postVotes);
                post.getDisLikesUsers().add(postVotes);
                return new AnswerDto(true);
            }

            if (votes.getValue() == (short) -1) {
                log.info("dislike уже существует " + userId + " пост " + likeDto.getPostId());
                return new AnswerDto(false);
            } else{
                votes.setValue((short) -1);
                postVotesRepository.save(votes);
                return new AnswerDto(true);
            }

        }




    private PostDto convertToDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        log.info(post.getLikesUsers().size() + " лайков записан в бд like ");
        postDto.setLikeCount(post.getLikesUsers().size());
        log.info(post.getDisLikesUsers().size() + " диздайков записан в бд like ");
        postDto.setDislikeCount(post.getDisLikesUsers().size());
        postDto.setAnnounce(post.getText());
        postDto.setCommentCounts(post.getComments().size());
        log.info(postDto.toString());
        return postDto;
    }
}
