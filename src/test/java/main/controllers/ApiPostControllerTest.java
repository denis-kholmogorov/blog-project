package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.security.ProviderToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.yml")
//@WithMockUser("ROLE_USER")
@AutoConfigureMockMvc
public class ApiPostControllerTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProviderToken providerToken;

    private static MockHttpSession session = new MockHttpSession();

    private static final ObjectMapper om = new ObjectMapper();


    @Before
    public void setUp() throws Exception {
        String id = session.getId();
        providerToken.createToken(id, 1);
    }

    @Test
    @Sql(scripts = "classpath:/clean_up.sql")
    @Sql(scripts = {"classpath:/insert_bd.sql"},
            config = @SqlConfig(encoding = "utf-8",
                    transactionMode = SqlConfig.TransactionMode.ISOLATED),
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void listPost() throws Exception {


        ResultActions perform = mvc.perform(get("/api/post")
                .accept(MediaType.APPLICATION_JSON)
               // .cookie(new Cookie("JSESSIONID", session))
                .session(session)
                .param("offset","0")
                .param("limit", "10")
                .param("mode", "recent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.posts[0].id", is(3)))
                /*.andExpect(jsonPath("$.data[0].user.id", is(3)))
                .andExpect(jsonPath("$.data[0].user.name", is("user2")))/*
                .andExpect(jsonPath("$.data[0].last_message.author.email", is("test1@mail.ru")))
                .andExpect(jsonPath("$.data[0].last_message.recipient.email", is("test2@mail.ru")))
                .andExpect(jsonPath("$.data[0].last_message.message_text", is("hello world")))*/;
    }

    @Test
    public void listPostByDate() {
    }

    @Test
    public void listPostByTag() {
    }

    @Test
    public void postById() throws Exception {


        ResultActions perform = mvc.perform(get("/api/post/1")
                .accept(MediaType.APPLICATION_JSON)
                //.cookie(new Cookie("JSESSIONID", session))
                .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time", is("01.04.2020 19:23:01")))
                .andExpect(jsonPath("$.title", is("post of admin")))
                .andExpect(jsonPath("$.text", is("post of admin")))
                .andExpect(jsonPath("$.comments[0].id",is(1)));
    }

    @Test
    public void getPostsBySearch() {
    }

    @Test
    public void getMyPosts() {
    }

    @Test
    public void getMyModerationPosts() {
    }

    @Test
    public void createPost() {
    }

    @Test
    public void changePost() {
    }

    @Test
    public void setLikePost() {
    }

    @Test
    public void setDislikePost() {
    }
}