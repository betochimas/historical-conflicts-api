package com.betochimas.historical_conflicts_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OpenApiIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public OpenApiIntegrationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testThatOpenApiJsonIsServed() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/api/nations']").exists())
                .andExpect(jsonPath("$.paths['/api/conflicts']").exists())
                .andExpect(jsonPath("$.paths['/api/battles']").exists())
                .andExpect(jsonPath("$.paths['/api/conflict-participants']").exists());
    }
}
