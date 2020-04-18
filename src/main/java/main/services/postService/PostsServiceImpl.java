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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl implements PostService {

    @Value("${post.min-length-title}")
    private int maxLenTitle;

    @Value("${post.min-length-text}")
    private int maxLenText;



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
        Integer idUser = providerToken.getUserIdBySession(session.getId());
        Post post = postRepository.findById(id).orElseThrow(BadRequestException::new);

        if (idUser != null && providerToken.validateToken(session.getId())) {
            User user = userRepository.findById(idUser).orElseThrow(BadRequestException::new);
            if(!user.getId().equals(post.getUser().getId())){
                post.setViewCount(post.getViewCount() + 1);
                post = postRepository.save(post);
            }
        }
        post.getComments().forEach(e -> e.getUser().setPhoto(""));
        PostDtoId postDtoId = modelMapper.map(post, PostDtoId.class);
        postDtoId.setTags(post.getSetTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        postDtoId.setLikeCount(post.getLikesUsers().size());
        postDtoId.setDislikeCount(post.getDisLikesUsers().size());

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

        int userId = providerToken.getAuthUserIdBySession(sessionId);
        Pageable paging = PageRequest.of((offset/limit), limit);
        Page<Post> page;
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
                .map(MyPostDto::new)
                .collect(Collectors.toList());
        log.info("количество записей " + userId + " равно " + myPosts.size());
        ListPostsDto listPostsDto = new ListPostsDto(myPosts);
        listPostsDto.setCount((int)page.getTotalElements());
        return listPostsDto;
    }

    public ListPostsDto getMyModerationPosts(int offset, int limit, String status, String sessionId){

        int userId = providerToken.getAuthUserIdBySession(sessionId);
        Pageable paging = PageRequest.of((offset/limit), limit, Sort.by("time").descending());
        List<PostModerationDto> postsList;
        Page<Post> posts;
        if(status.equals("new")) {
            posts = postRepository.findModerationNewPosts(paging);
        }else{
            posts = postRepository.findMyModerationPosts(userId, status, paging);

        }
        postsList = posts.stream()
                .map(PostModerationDto::new)
                .collect(Collectors.toList());
        ListPostsDto listPostsDto = new ListPostsDto(postsList);
        listPostsDto.setCount((int)posts.getTotalElements());
        return listPostsDto;
    }

    public AnswerDto createPost(RequestPostDto postDto, String session) throws ParseException {

        Integer userId = providerToken.getAuthUserIdBySession(session);
        User user = userRepository.findById(userId).orElseThrow(BadRequestException::new);

        GlobalSettings settings = globalSettingsRepository.findByCode("MULTIUSER_MODE").orElseThrow(BadRequestException::new);
        if(settings.isValue() || user.getIsModerator() == (byte)1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatTime = postDto.getTime().replace("T", " ");
            Calendar time = Calendar.getInstance();
            time.setTime(sdf.parse(formatTime));
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 1);
            if (time.before(now)) {
                time = Calendar.getInstance();
            }
            if (postDto.getTitle().length() < maxLenTitle) throw new BadRequestException("Заголовок меньше " + maxLenTitle + " символов");
            if (postDto.getText().length() < maxLenText) throw new BadRequestException("Текст меньше " + maxLenText + " символов");

            Post post = new Post();
            post.setUser(user);
            post.setIsActive(postDto.getActive());
            post.setTime(time);
            post.setText(postDto.getText());
            post.setTitle(postDto.getTitle());
            post.setViewCount(0);
            post.setModerationStatus(ModerationStatus.NEW);
            postRepository.save(post);
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
        }
        throw new BadRequestException("Публикация постов запрещена модератором");
    }

    public AnswerDtoInterface changePost(Integer id, RequestPostDto postDto, String session)  {

        providerToken.getAuthUserIdBySession(session);

        Post post = postRepository.findById(id).orElseThrow(CustomNotFoundException::new);
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
        if(time.before(now)) {
            time = Calendar.getInstance();
        }

        if (postDto.getTitle().length() < maxLenTitle) throw new BadRequestException("Заголовок меньше " + maxLenTitle + " символов");
        if (postDto.getText().length() < maxLenText) throw new BadRequestException("Текст меньше " + maxLenText + " символов");

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
            }
        });
        return new AnswerDto(true);
    }

    public AnswerDto setLikePost(LikeRequestDto likeDto, HttpSession session) {
        Integer userId = providerToken.getAuthUserIdBySession(session.getId());
        Post post = postRepository.findById(likeDto.getPostId()).orElseThrow(CustomNotFoundException::new);
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
            Integer userId = providerToken.getAuthUserIdBySession(session.getId());
            PostVotes votes = postVotesRepository.findByPostIdAndUserId(likeDto.getPostId(), userId).orElse(null);
            Post post = postRepository.findById(likeDto.getPostId()).orElseThrow(CustomNotFoundException::new);
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
