package main.services.postService;

import lombok.extern.slf4j.Slf4j;
import main.CustomException.BadRequestException;
import main.CustomException.CustomNotFoundException;
import main.DTOEntity.*;
import main.DTOEntity.PostDtoInterface.AnswerDtoInterface;
import main.DTOEntity.request.RequestPostDto;
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
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public PostsServiceImpl(PostRepository postRepository,
                            ModelMapper modelMapper,
                            ProviderToken providerToken,
                            UserRepository userRepository,
                            TagRepository tagRepository,
                            TagToPostRepository tagToPostRepository,
                            PostVotesRepository postVotesRepository,
                            GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.providerToken = providerToken;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.postVotesRepository = postVotesRepository;
        this.globalSettingsRepository = globalSettingsRepository;
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


    public PostDtoId findPostById(Integer id, HttpSession session) {
        log.info(" запрос поста " + id);
        User user = userRepository.findById(providerToken.getUserIdBySession(session.getId())).orElseThrow(BadRequestException::new);
        Post post = postRepository.findById(id).orElseThrow(BadRequestException::new);

        /*DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 1);
        log.info(post.getId() + " id поста" + post.getViewCount());*/

        PostDtoId postDtoId = modelMapper.map(post, PostDtoId.class);
        postDtoId.setTags(post.getSetTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        postDtoId.setLikeCount(post.getLikesUsers().size());
        postDtoId.setDislikeCount(post.getDisLikesUsers().size());

        if (providerToken.validateToken(session.getId()) && !user.getId().equals(post.getUser().getId())) {
            postDtoId.setViewCount(post.getViewCount());
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        return postDtoId;
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
            Page<Post> page = null;
            byte isActive = 0;
            ModerationStatus moderationStatus = null;
            switch (status) {
                case "inactive":
                    isActive = (byte)0;
                    break;
                case "pending":
                    isActive = (byte)1;
                    moderationStatus = ModerationStatus.NEW;
                    break;
                case "declined":
                    isActive = (byte)1;
                    moderationStatus = ModerationStatus.DECLINED;
                    break;
                case "published":
                    isActive = (byte)1;
                    moderationStatus = ModerationStatus.ACCEPTED;
                    break;
            }
            if(moderationStatus == null) {
                 page = postRepository.findMyPosts(userId, isActive, paging);
            }
            else {
                page = postRepository.findMyPosts(userId, isActive, moderationStatus, paging);
            }
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

    public AnswerDtoInterface createPost(RequestPostDto postDto, String session) throws ParseException {
        providerToken.validateToken(session);
        Integer userId = providerToken.getUserIdBySession(session);
        User user = userRepository.findById(userId).orElseThrow(BadRequestException::new);
        Map<String, String> error = new HashMap<>();

        GlobalSettings settings = globalSettingsRepository.findById(1).get();
        if(settings.isValue() || user.getIsModerator() == (byte)1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatTime = postDto.getTime().replace("T", " ");
            Calendar time = Calendar.getInstance();
            time.setTime(sdf.parse(formatTime));
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 1);
            log.info(time.toString() + " время установленное постом");
            if (time.before(now)) {
                time = Calendar.getInstance();
            }
            if (postDto.getTitle().length() >= 10) {
                if (postDto.getText().length() >= 5) {

                    Post post = new Post();
                    post.setUser(user);
                    post.setIsActive(postDto.getActive());
                    post.setTime(time);
                    post.setText(postDto.getText());
                    post.setTitle(postDto.getTitle());
                    post.setViewCount(0);
                    post.setModerationStatus(ModerationStatus.NEW);
                    Post p = postRepository.save(post);
                    postDto.getTags().forEach(t -> {
                        Tag tag = tagRepository.findByName(t).orElse(null);
                        TagToPost tp = new TagToPost();
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(t);
                            Tag newTag = tagRepository.save(tag);
                            tp.setTag_id(newTag.getId());
                        } else {
                            tp.setTag_id(tag.getId());
                        }
                        tp.setPost_id(post.getId());
                        tp.setId((int) tagToPostRepository.count() + 1);
                        tagToPostRepository.save(tp);
                        log.info(tag.getName());

                    });

                    return new AnswerDto(true);
                } else {
                    error.put("title", "Текст публикации");
                    error.put("text", "Текст публикации слишком короткий");
                }
            } else {
                error.put("title", "Заголовок публикации");
                error.put("text", "Заголовок слишком короткий или его нет");
            }
            return new AnswerErrorDto(false, error);
        }
        error.put("title", "Запрет публикация постов");
        error.put("text", "Публикация постов запрещена");
        return new AnswerErrorDto(false, error);
    }

    public AnswerDtoInterface changePost(Integer id, RequestPostDto postDto, String session)  {
        if(providerToken.validateToken(session)) {
            log.info(postDto.getTime());
            Post post = postRepository.findById(id).get();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatTime = postDto.getTime().replace("T"," ");
            Calendar time = Calendar.getInstance();
            try {
                time.setTime(sdf.parse(formatTime));
            } catch (ParseException e) {
                throw new BadRequestException("Неверно установлена дата");
            }
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 1);
            log.info(time.toString() + " время установленное постом");
            if(time.before(now)) {
                time = Calendar.getInstance();
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
                        else if(tag == null){
                            Tag newTag = new Tag();
                            newTag.setName(t);
                            int tagId = tagRepository.save(newTag).getId();
                            TagToPost tp = new TagToPost();
                            tp.setTag_id(tagId);
                            tp.setPost_id(post.getId());
                            tp.setId((int) tagToPostRepository.count() + 1);
                            tagToPostRepository.save(tp);
                            log.info(tag.getName());
                        }
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
            return new AnswerErrorDto(false, error);
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
                return new AnswerDto(false);
            } else{
                votes.setValue((short) -1);
                postVotesRepository.save(votes);
                return new AnswerDto(true);
            }

        }

    private PostDto convertToDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setLikeCount(post.getLikesUsers().size());
        postDto.setDislikeCount(post.getDisLikesUsers().size());
        postDto.setAnnounce(post.getText());
        postDto.setCommentCount(post.getComments().size());
        return postDto;
    }
}
