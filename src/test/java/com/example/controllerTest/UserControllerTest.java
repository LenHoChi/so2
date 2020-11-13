package com.example.controllerTest;

import com.example.SocialNetworkApplication;
import com.example.TestRepositoryConfig;
import com.example.controller.UserController;
import com.example.dto.UserDTO;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
//@TestPropertySource(locations="classpath:application-test.properties")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserDTO userDTOA = new UserDTO("len1");
        UserDTO userDTOB = new UserDTO("len2");
        List<UserDTO> userDTOList = Arrays.asList(userDTOA, userDTOB);
        //when(userService.getAllUsers()).thenReturn(userDTOList);
        given(userService.getAllUsers()).willReturn(userDTOList);
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.message").value("success"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email", is("len1")))
                .andExpect(jsonPath("$[1].email", is("len2")))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetUser() throws Exception {
        UserDTO userDTO = new UserDTO("len1");
        when(userService.getUserById(userDTO.getEmail())).thenReturn(Optional.of(userDTO));
        mockMvc.perform(get("/api/users/{id}", userDTO.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("len1")))
                .andExpect(content().contentType("application/json"));
        verify(userService, times(1)).getUserById(userDTO.getEmail());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO("newmooncsu@gmail.com");

        when(userService.saveUser(Mockito.any(UserDTO.class))).thenReturn(userDTO);
        // given(userService.saveUser(any(UserDTO.class))).willReturn(userDTO);
        mockMvc.perform(post("/api/users")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.email", is("newmooncsu@gmail.com")));
        verify(userService, times(1)).saveUser(userDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testDeleteUser() throws Exception {
        UserDTO userDTO = new UserDTO("len1");
        Map<String, Boolean> response = new HashMap<>();
        response.put("delete ok", Boolean.TRUE);
        //doNothing().when(userService.deleteUser(userDTO.getEmail()));
        when(userService.deleteUser(userDTO.getEmail())).thenReturn(response);
        MvcResult len = mockMvc.perform(delete("/api/users/{id}", userDTO.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        verify(userService, times(1)).deleteUser(userDTO.getEmail());
        verifyNoMoreInteractions(userService);
    }

}
