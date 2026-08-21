package ru.practicum.request;

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
import ru.practicum.gateway.request.ItemRequestClient;
import ru.practicum.gateway.request.ItemRequestController;
import ru.practicum.gateway.request.dto.ItemRequestRequestDto;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = Gateway.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestRequestDto validDto;
    private ItemRequestRequestDto invalidDto;
    private final long userId = 1L;
    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        validDto = new ItemRequestRequestDto("Нужна стремянка на 3 дня");
        invalidDto = new ItemRequestRequestDto("   ");
    }

    @Test
    void createItemRequest_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemRequestClient.createItemRequest(eq(userId), any(ItemRequestRequestDto.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest_withInvalidDto_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(itemRequestClient);
    }

    @Test
    void getItemRequestsByUser_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemRequestClient.getItemRequestsByUser(userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests_withValidPagination_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemRequestClient.getAll(userId, 0, 10)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests_withNegativeFrom_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-5")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemRequests_withNegativeSize_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequest_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemRequestClient.getItemRequest(requestId, userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}