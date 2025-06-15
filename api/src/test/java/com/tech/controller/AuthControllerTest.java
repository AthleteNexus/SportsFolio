//package com.tech.controller;
//
//import junit.framework.TestCase;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//public class AuthControllerTest extends TestCase {
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @Test
//    public void testLoginReturnsOk() throws Exception {
//        String json = "{\"username\":\"user\",\"password\":\"password\"}";
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isOk());
//    }
//}