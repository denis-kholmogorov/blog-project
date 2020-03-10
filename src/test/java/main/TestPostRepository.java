package main;

import main.DTOEntity.TagDto;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.repositories.TagRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@TestPropertySource("classpath:/application.yml")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPostRepository
{
    @Autowired
    PostRepository postRepository;

    @Autowired
    TagRepository tagRepository;

    Pageable paging = PageRequest.of(0, 10);

    @Test
    @Sql(scripts = "classpath:/clean_up.sql")
    @Sql(scripts = {"classpath:/insert_bd.sql"},
            config = @SqlConfig(encoding = "utf-8",
            transactionMode = SqlConfig.TransactionMode.ISOLATED),
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testFindAllPostById()
    {
        int id = 1;
        Optional<Post> post = postRepository.findPostById((byte) 1, ModerationStatus.ACCEPTED, id);
        assertEquals(1, (int)post.get().getId());
        assertEquals("post of admin", post.get().getText());
    }

    @Test
    public void testFindAllPostsByTag(){
        String tag  = "Java";
        List<Post> posts = postRepository.findAllPostsByTag((byte) 1, ModerationStatus.ACCEPTED.toString(),tag, paging).getContent();
        assertEquals(1, (int)posts.get(0).getId());
    }

    @Test
    public void testFindAllPostsBySearch(){
        String query = "admin";
        List<Post> posts = postRepository.findPostBySearch((byte) 1, ModerationStatus.ACCEPTED, query, paging).getContent();
        assertEquals(1, posts.size());
        assertEquals("post of admin", posts.get(0).getText());
        assertEquals("post of admin", posts.get(0).getTitle());
    }

    @Test
    public void testFindPostsByDate(){
        String date = "2020-01-22";
        List<Post> posts = postRepository.findAllPostsByDate((byte) 1, ModerationStatus.ACCEPTED.toString(), date, paging).getContent();
        assertEquals(1, posts.size());
        assertEquals("user1", posts.get(0).getUser().getName());
    }

    @Test
    public void testFindPostsByDateAfterCurTime(){
        String date = "2020-03-23";
        List<Post> posts = postRepository.findAllPostsByDate((byte) 1, ModerationStatus.ACCEPTED.toString(), date, paging).getContent();
        assertEquals(0, posts.size());
    }

    @Test
    public void testFindAllPostsAndSort(){
        Sort sort = Sort.by(Sort.Direction.DESC, "likesUsers.size");
        Pageable pagingWithSort = PageRequest.of(0, 10, sort);
        Page<Post> postsPage1 = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, pagingWithSort);
        List<Post> posts1 = postsPage1.toList();
        assertEquals(1, (int)posts1.get(0).getId());
        assertEquals(3, posts1.get(0).getLikesUsers().size());
        assertEquals(2, posts1.size());

        sort = Sort.by(Sort.Direction.DESC, "comments.size");
        pagingWithSort = PageRequest.of(0, 10, sort);
        Page<Post> postsPage = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, pagingWithSort);
        List<Post> posts2 = postsPage.toList();
        assertEquals(1, (int)posts2.get(0).getId());
        assertEquals("post of admin", posts2.get(0).getText());
        assertEquals(2, posts2.size());

       /* sort = Sort.by(Sort.Direction.ASC, "time");
        pagingWithSort = PageRequest.of(0, 10, sort);
        List<Post> posts3 = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, pagingWithSort);
        assertEquals(1, (int)posts3.get(0).getId());
        assertEquals("post of admin", posts3.get(0).getText());
        assertEquals(2, posts3.size());

        sort = Sort.by(Sort.Direction.DESC, "time");
        pagingWithSort = PageRequest.of(0, 10, sort);
        List<Post> posts4 = postRepository.findDistinctByActiveAndModerationStatus((byte) 1, ModerationStatus.ACCEPTED, pagingWithSort);
        assertEquals(2, (int)posts4.get(0).getId());
        assertEquals("post of user", posts4.get(0).getText());
        assertEquals(2, posts4.size());*/

    }

    @Test
    public void testFindAllYearWithPosts() {
        List <Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        assertEquals(1, allYearsWithPosts.size());
        assertEquals(2020, (int)allYearsWithPosts.get(0));
    }

    @Test
    public void testFindAllYearWithPostsByYear() {
        Integer year = 2020;
        List<String>listDateAndCount = postRepository.findCountPostForCalendar(year);
        assertEquals(2, listDateAndCount.size());
    }



    @Test
    public void testFindTagsByQuery(){
        String query = "java";
        List<TagDto> list = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        assertEquals(2, list.size());

        double maxWeight = list.get(0).getWeight();
        list.forEach(tagDto -> tagDto.setWeight(tagDto.getWeight()/maxWeight));
        if(!query.isEmpty())
        {
            list = list.stream().filter(t-> t.getName().toLowerCase().contains(query)).collect(Collectors.toList());
        }
        assertEquals(1, list.size());
        assertEquals("java", list.get(0).getName());
    }




}

