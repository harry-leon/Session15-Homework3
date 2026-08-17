package com.se191116.mvc.homework1.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void customerCanCreateOrderAndViewOwnHistory() throws Exception {
        String payload = """
                {
                  \"items\": [
                    {\"productId\": 1, \"quantity\": 2},
                    {\"productId\": 2, \"quantity\": 1}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer1", "customer123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("customer1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalMoney").value(110000));

        mockMvc.perform(get("/api/orders/my")
                        .with(httpBasic("customer1", "customer123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("customer1"));
    }

    @Test
    void staffCanViewAllOrdersAndUpdateStatus() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer1", "customer123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"items\": [
                                    {\"productId\": 3, \"quantity\": 1}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders")
                        .with(httpBasic("staff1", "staff123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        mockMvc.perform(put("/api/orders/1/status")
                        .with(httpBasic("staff1", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { \"status\": \"CONFIRMED\" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void nonCustomerCannotCreateOrderAndAdminCannotUpdateStatus() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("staff1", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"items\": [
                                    {\"productId\": 1, \"quantity\": 1}
                                  ]
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/orders/1/status")
                        .with(httpBasic("admin1", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { \"status\": \"COMPLETED\" }
                                """))
                .andExpect(status().isForbidden());
    }
}
