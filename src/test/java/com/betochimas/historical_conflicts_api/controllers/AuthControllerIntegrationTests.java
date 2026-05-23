package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.auth.Role;
import com.betochimas.historical_conflicts_api.auth.UserEntity;
import com.betochimas.historical_conflicts_api.auth.UserRepository;
import com.betochimas.historical_conflicts_api.auth.dto.LoginRequest;
import com.betochimas.historical_conflicts_api.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthControllerIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper,
                                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private RegisterRequest validRegister() {
        return RegisterRequest.builder()
                .username("alice")
                .email("alice@example.com")
                .password("alicepass1")
                .build();
    }

    @Test
    public void testThatRegisterReturnsHttp201AndAccessToken() throws Exception {
        String json = objectMapper.writeValueAsString(validRegister());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.expiresAt").isString());
    }

    @Test
    public void testThatRegisterDuplicateUsernameReturnsHttp409() throws Exception {
        String json = objectMapper.writeValueAsString(validRegister());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("username already exists: alice"));
    }

    @Test
    public void testThatLoginReturnsHttp200AndAccessTokenWithValidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder().username("alice").password("alicepass1").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString());
    }

    @Test
    public void testThatLoginReturnsHttp401WithWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder().username("alice").password("wrongpass").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testThatLoginReturnsHttp401ForDisabledUserWithCorrectPassword() throws Exception {
        // No API disables an account, so create one directly. Correct password is supplied,
        // so a 401 here proves the disabled-account check (not a password mismatch) rejected the login.
        UserEntity disabled = new UserEntity(
                "alice", "alice@example.com", passwordEncoder.encode("alicepass1"), Role.USER);
        disabled.setEnabled(false);
        userRepository.save(disabled);

        LoginRequest login = LoginRequest.builder().username("alice").password("alicepass1").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testThatProtectedEndpointReturns401WithoutToken() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(post("/api/nations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
