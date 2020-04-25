package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import main.security.ProviderToken;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .param("year","2020")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].name",is("python")))
                .andExpect(jsonPath("$.tags[1].name",is("java")))
                .andExpect(jsonPath("$.tags[2].name",is("kotlin")));
    }

    @Test
    void getAllStatistics() {
    }

    @Test
    void getMyStatistics() {
    }

    @Test
    void uploadImage() {
    }

    @Test
    void getGlobalSettings() {
    }

    @Test
    void setGlobalSettings() {
    }

    @Test
    void setModerationAction() {
    }

    @Test
    void setComments() {
    }

    @Test
    void setMyProfileWithPhoto() {
    }

    @Test
    void setMyProfile() {
    }
}