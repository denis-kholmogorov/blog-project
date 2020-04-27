package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import main.DTOEntity.ModerationDecisionDto;
import main.DTOEntity.SettingsDto;
import main.DTOEntity.request.RequestCommentsDto;
import main.DTOEntity.request.RequestProfileDto;
import main.security.ProviderToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.yml")
@Sql(value = {"/insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
class ApiGeneralControllerTest {

    private MockMvc mvc;

    private ProviderToken providerToken;

    private static MockHttpSession session = new MockHttpSession();

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    ApiGeneralControllerTest(MockMvc mvc, ProviderToken providerToken) {
        this.mvc = mvc;
        this.providerToken = providerToken;
    }

 /*   @BeforeEach
    void setUp() {
        providerToken.createToken("1",1);
    }*/

    @AfterEach
    void afterAll() {
        providerToken.deleteToken("1");
    }

    @Test
    @SneakyThrows
    void init() {
        ResultActions perform = mvc.perform(get("/api/init")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Once upon a time in Java")))
                .andExpect(jsonPath("$.email",is("dendiesel88@yandex.ru")))
                .andExpect(jsonPath("$.copyrightFrom",is("2020")));
    }

    @Test
    @SneakyThrows
    void tagBySearchJava() {
        ResultActions perform = mvc.perform(get("/api/tag")
                .param("query","Java")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").exists())
                .andExpect(jsonPath("$.tags[0].name",is("java")))
                .andExpect(jsonPath("$.tags[0].weight",is( 0.4999999250000038)));
    }

    @Test
    @SneakyThrows
    void tagBySearchNonParam() {
        ResultActions perform = mvc.perform(get("/api/tag")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].name",is("python")))
                .andExpect(jsonPath("$.tags[1].name",is("java")))
                .andExpect(jsonPath("$.tags[2].name",is("kotlin")));
    }

    @Test
    @SneakyThrows
    void postsByCalendar2020() {
        ResultActions perform = mvc.perform(get("/api/calendar")
                .param("year","")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years", hasSize(1)))
                .andExpect(jsonPath("$.posts.2020-04-01",is(1)))
                .andExpect(jsonPath("$.posts.2020-04-02",is(1)))
                .andExpect(jsonPath("$.posts.2020-04-03",is(3)))
                .andExpect(jsonPath("$.posts.2020-04-10",is(1)));
    }

    @Test
    @SneakyThrows
    void getMyStatistics() {
      //  providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(get("/api/statistics/my")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postsCount",is(1)))
                .andExpect(jsonPath("$.likesCount",is(3)))
                .andExpect(jsonPath("$.dislikesCount",is(0)))
                .andExpect(jsonPath("$.viewsCount",is(10)))
                .andExpect(jsonPath("$.firstPublication", is("01.04.2020 22:23:01")));
    }

    @Test
    @SneakyThrows
    void getAllStatistics() {
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(get("/api/statistics/all")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postsCount",is(6)))
                .andExpect(jsonPath("$.likesCount",is(6)))
                .andExpect(jsonPath("$.dislikesCount",is(0)))
                .andExpect(jsonPath("$.viewsCount",is(60)))
                .andExpect(jsonPath("$.firstPublication", is("01.04.2020 22:23:01")));
    }

    @Test
    @SneakyThrows
    void getGlobalSettings() {
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(get("/api/settings")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MULTIUSER_MODE",is(true)))
                .andExpect(jsonPath("$.POST_PREMODERATION",is(true)))
                .andExpect(jsonPath("$.STATISTICS_IS_PUBLIC",is(true)));
    }

    @Test
    @SneakyThrows
    void setGlobalSettings() {
        SettingsDto settings = new SettingsDto(false,false, false);
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(settings))
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void setGlobalSettingsError() {
        SettingsDto settings = new SettingsDto(false,false, false);
        providerToken.createToken("1",2);
        ResultActions perform = mvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(settings))
                .session(session))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @SneakyThrows
    void setModerationAction() {
        providerToken.createToken("1", 1);
        ModerationDecisionDto decision = new ModerationDecisionDto(4,"accepted");
        ResultActions perform = mvc.perform(post("/api/moderation")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(decision)))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    @SneakyThrows
    void setComments() {
        RequestCommentsDto comment = new RequestCommentsDto(null,3,"Отличный пост");
        providerToken.createToken("1", 1);
        ResultActions perform = mvc.perform(post("/api/comment")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(comment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(5)));
    }

    @Test
    @SneakyThrows
    void setCommentsErrorLength() {
        RequestCommentsDto comment = new RequestCommentsDto(null,3,"Отлич");
        providerToken.createToken("1", 1);
        ResultActions perform = mvc.perform(post("/api/comment")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(comment)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.result",is(false)))
                .andExpect(jsonPath("$.errors.text").exists());
    }

    @Test
    @SneakyThrows
    void setMyProfile() {
        RequestProfileDto profile = new RequestProfileDto(1,
                "anyName","newadmin@admin.ru","newpassword");
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(post("/api/profile/my")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(profile)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)));
    }

    @Test
    @SneakyThrows
    void setMyProfileErrorName() {
        RequestProfileDto profile = new RequestProfileDto(1,
                "a","newadmin@admin.ru","newpassword");
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(post("/api/profile/my")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(profile)))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.result",is(false)))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @SneakyThrows
    void setMyProfileErrorEmail() {
        RequestProfileDto profile = new RequestProfileDto(1,
                "newAdmin","admin@admin.ru","newpassword");
        providerToken.createToken("1",2);
        ResultActions perform = mvc.perform(post("/api/profile/my")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(profile)))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.result",is(false)))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @SneakyThrows
    void setMyProfileErrorPass() {
        RequestProfileDto profile = new RequestProfileDto(1,
                "newAdmin","admin@admin.ru","new");
        providerToken.createToken("1",1);
        ResultActions perform = mvc.perform(post("/api/profile/my")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(profile)))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.result",is(false)))
                .andExpect(jsonPath("$.errors.password").exists());
    }



}