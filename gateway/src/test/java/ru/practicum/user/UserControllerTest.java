package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.Gateway;
import ru.practicum.gateway.user.UserClient;
import ru.practicum.gateway.user.UserController;
import ru.practicum.gateway.user.dto.UpdateUserDto;
import ru.practicum.gateway.user.dto.UserRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = Gateway.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private UserRequestDto validCreateDto;
    private UserRequestDto invalidCreateDto;
    private UpdateUserDto validUpdateDto;
    private UpdateUserDto invalidUpdateDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        validCreateDto = new UserRequestDto("Ivan", "ivan@mail.com");
        invalidCreateDto = new UserRequestDto("  ", "not-an-email");

        validUpdateDto = new UpdateUserDto(userId, "Ivan Updated", "updated@mail.com");
        invalidUpdateDto = new UpdateUserDto(userId, "Ivan", "invalid-email-format");
    }

    @Test
    void getUser_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(userClient.getUser(userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(userClient.createUser(any(UserRequestDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_withInvalidDto_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCreateDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userClient);
    }

    @Test
    void updateUser_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(userClient.updateUser(eq(userId), any(UpdateUserDto.class))).thenReturn(responseEntity);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_withInvalidEmail_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userClient);
    }

    @Test
    void deleteUser_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(userClient.deleteUser(userId)).thenReturn(responseEntity);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
    }
}