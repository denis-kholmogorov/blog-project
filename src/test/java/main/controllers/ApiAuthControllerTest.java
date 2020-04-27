package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import main.DTOEntity.request.RequestLoginDto;
import main.DTOEntity.request.RequestRegisterDto;
import main.security.ProviderToken;
import org.junit.jupiter.api.AfterEach;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.yml")
@Sql(value = {"/insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
class ApiAuthControllerTest {

    private MockMvc mvc;

    private ProviderToken providerToken;

    private static MockHttpSession session = new MockHttpSession();

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    ApiAuthControllerTest(MockMvc mvc, ProviderToken providerToken) {
        this.mvc = mvc;
        this.providerToken = providerToken;
    }

    @AfterEach
    void afterAll() {
        providerToken.deleteToken("1");
    }

    @Test
    @SneakyThrows
    void captcha(){
        ResultActions perform = mvc.perform(get("/api/auth/captcha")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.image").exists());
    }

    @Test
    @SneakyThrows
    void register() {
        RequestRegisterDto register = new RequestRegisterDto(
                "register@mail.ru",
                "password",
                "Register",
                "12u3h",
                "-23693860"
        );
        ResultActions perform = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(register))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)));

    }
    /** Необходимо установить user и password для отправки пароля в application-context*/
   /* @Test
    @SneakyThrows
    void restore() {
        RequestRestoreDto restore = new RequestRestoreDto("admin@admin.ru");
        ResultActions perform = mvc.perform(post("/api/auth/restore")
                .content(om.writeValueAsString(restore))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)));
    }*/

    @Test
    @SneakyThrows
    void login() {
        RequestLoginDto login = new RequestLoginDto("admin@admin.ru", "adminadmin");
        ResultActions perform = mvc.perform(post("/api/auth/login")
                .content(om.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("admin")))
                .andExpect(jsonPath("$.user.email", is("admin@admin.ru")));
    }

    @Test
    @SneakyThrows
    void check() {
        providerToken.createToken("1", 1);
        ResultActions perform = mvc.perform(get("/api/auth/check")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("admin")))
                .andExpect(jsonPath("$.user.email", is("admin@admin.ru")));
    }


    @Test
    @SneakyThrows
    void logout() {
        providerToken.createToken("1", 1);
        ResultActions perform = mvc.perform(get("/api/auth/logout")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)));
    }
}