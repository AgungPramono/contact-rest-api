package com.agung.restful.controller;

import com.agung.restful.entity.User;
import com.agung.restful.model.LoginUserRequest;
import com.agung.restful.model.TokenResponse;
import com.agung.restful.model.WebResponse;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    @Test
    void userLoginNotFound() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUserName("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setUsername("user-test");
        user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
        user.setName("user");

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUserName("user-test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception {
        User user = new User();
        user.setUsername("user-test");
        user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
        user.setName("user");

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUserName("user-test");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("user-test").orElse(null);
            assertNotNull(userDb);
            assertEquals(userDb.getToken(),response.getData().getToken());
            assertEquals(userDb.getTokenExpiredAt(),response.getData().getExpiredAt());
        });
    }

    @Test
    void logoutFailed() throws Exception {
        User user = new User();
        user.setUsername("user-test");
        user.setPassword("rahasia12345");

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test-token");
        user.setTokenExpiredAt(System.currentTimeMillis() + (60 * 60 * 1000));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN","test-token")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNull(response.getErrors());
            assertEquals("Ok",response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertNull(userDb.getToken());
            assertNull(userDb.getTokenExpiredAt());

        });
    }
}