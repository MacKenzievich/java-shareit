package ru.practicum.server.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItServer.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UpdateUserDto updateUserDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(userId, "Иван", "ivan@mail.com");
        updateUserDto = new UpdateUserDto(userId, "Иван Обновленный", "ivan_new@mail.com");
    }

    @Test
    void createUser_shouldReturnStatus201AndUserDto() throws Exception {
        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser_shouldReturnStatus200AndUpdatedUserDto() throws Exception {
        Mockito.when(userService.updateUser(eq(userId), any(UpdateUserDto.class))).thenReturn(updateUserDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updateUserDto.getId()))
                .andExpect(jsonPath("$.name").value(updateUserDto.getName()))
                .andExpect(jsonPath("$.email").value(updateUserDto.getEmail()));
    }

    @Test
    void getUser_shouldReturnStatus200AndUserDto() throws Exception {
        Mockito.when(userService.getUser(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void deleteUser_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
    }
}