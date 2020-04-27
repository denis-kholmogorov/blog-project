package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.DTOEntity.LikeRequestDto;
import main.DTOEntity.request.RequestPostDto;
import main.security.ProviderToken;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.yml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Sql(value = {"/insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class ApiPostControllerTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProviderToken providerToken;

    private static MockHttpSession session = new MockHttpSession();

    private static final ObjectMapper om = new ObjectMapper();

    @AfterEach
    void afterAll() {
        providerToken.deleteToken("1");
    }

    @Before
    public void setUp() throws Exception {
        String id = session.getId();
        providerToken.createToken(id, 1);
    }

    @Test
    public void getListPostByDate() throws Exception {
        ResultActions perform = mvc.perform(get("/api/post/byDate")
                .accept(MediaType.APPLICATION_JSON)
                .param("offset","0")
                .param("limit", "10")
                .param("date", "2020-04-03"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id", is(3)))
                .andExpect(jsonPath("$.posts[0].time", is("03.04.2020 22:23:01")))
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    public void getListPost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("сюда авторизацию", "сюда токен");

        ResultActions perform = mvc.perform(get("/api/post")
                .accept(MediaType.APPLICATION_JSON)
                .param("offset","0")
                .param("limit", "10")
                .param("mode", "recent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.posts[0].id", is(3)));
    }



    @Test
    public void getListPostByTag() throws Exception {
        ResultActions perform = mvc.perform(get("/api/post/byTag")
                .accept(MediaType.APPLICATION_JSON)
                .param("offset","0")
                .param("limit", "10")
                .param("tag", "python"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.posts", hasSize(2)));

    }

    @Test
    public void getPostById() throws Exception {
        ResultActions perform = mvc.perform(get("/api/post/1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("post of admin")))
                .andExpect(jsonPath("$.text", is("post of admin")))
                .andExpect(jsonPath("$.user.id",is(1)))
                .andExpect(jsonPath("$.user.name",is("admin")))
                .andExpect(jsonPath("$.tags", hasSize(2)));
    }

    @Test
    public void getPostsBySearch() throws Exception {
        ResultActions perform = mvc.perform(get("/api/post/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("offset","0")
                .param("limit", "10")
                .param("query", "post of admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id", is(1)))
                .andExpect(jsonPath("$.posts[0].announce", is("post of admin")));
    }

    @Test
    public void getMyAcceptedPosts() throws Exception {
        providerToken.createToken(session.getId(), 1);
        ResultActions perform = mvc.perform(get("/api/post/my")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .param("offset","0")
                .param("limit", "10")
                .param("status", "published"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id", is(1)))
                .andExpect(jsonPath("$.posts[0].announce", is("post of admin")));
    }

    @Test
    public void getMyDeclinedPosts() throws Exception {
        String id = "1";
        providerToken.createToken(id, 2);
        ResultActions perform = mvc.perform(get("/api/post/my")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .param("offset","0")
                .param("limit", "10")
                .param("status", "declined"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id", is(5)))
                .andExpect(jsonPath("$.posts[0].user.id", is(2)))
                .andExpect(jsonPath("$.posts[0].announce", is("post of user DECLINED")));
    }

    @Test
    public void getMyNewPosts() throws Exception {
        providerToken.createToken(session.getId(), 2);
        ResultActions perform = mvc.perform(get("/api/post/my")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .param("offset","0")
                .param("limit", "10")
                .param("status", "pending"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id", is(4)))
                .andExpect(jsonPath("$.posts[0].user.id", is(2)))
                .andExpect(jsonPath("$.posts[0].announce", is("post of user NEW")));
    }

    @Test
    public void getMyNonActivePosts() throws Exception {
        providerToken.createToken(session.getId(), 2);
        ResultActions perform = mvc.perform(get("/api/post/my")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .param("offset","0")
                .param("limit", "10")
                .param("status", "inactive"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].id", is(6)))
                .andExpect(jsonPath("$.posts[0].user.id", is(2)))
                .andExpect(jsonPath("$.posts[0].announce", is("non active post user")));
    }


    @Test
    public void postCreatePost() throws Exception {
        RequestPostDto dto = new RequestPostDto();
        dto.setActive((byte)1);
        Set<String> tag = new HashSet();
        tag.add("Java");
        dto.setTags(tag);
        dto.setTitle("New test");
        dto.setText("New test text");
        dto.setTime("2019-12-01 17:36:15");

        mvc.perform(post("/api/post/")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)));
    }

    @Test
    public void postSetLikePost() throws Exception {
        LikeRequestDto like = new LikeRequestDto();
        like.setPostId(2);

        mvc.perform(post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(like))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)));

    }

    @Test
    public void postSetErrorLikePost() throws Exception {
        LikeRequestDto like = new LikeRequestDto();
        like.setPostId(9);

        mvc.perform(post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(like))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postSetDislikePost() throws Exception {
        LikeRequestDto like = new LikeRequestDto();
        like.setPostId(2);

        mvc.perform(post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(like))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)));

    }

    @Test
    public void postSetErrorDislikePost() throws Exception {
        LikeRequestDto like = new LikeRequestDto();
        like.setPostId(9);

        mvc.perform(post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(like))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }


    @Test
    public void putChangePost() throws Exception {
        RequestPostDto dto = new RequestPostDto();
        dto.setActive((byte)1);
        Set<String> tag = new HashSet();
        tag.add("Java");
        dto.setTags(tag);
        dto.setTitle("New test");
        dto.setText("New test text");
        dto.setTime("2019-12-01 17:36:15");

        mvc.perform(put("/api/post/1")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)));

    }

    @Test
    public void putErrorChangePost() throws Exception {
        RequestPostDto dto = new RequestPostDto();
        dto.setActive((byte)1);
        Set<String> tag = new HashSet();
        tag.add("Java");
        dto.setTags(tag);
        dto.setTitle("N");
        dto.setText("New test text");
        dto.setTime("2019-12-01 17:36:15");

        mvc.perform(put("/api/post/1")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}