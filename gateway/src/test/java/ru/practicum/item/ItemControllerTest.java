package ru.practicum.item;


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
import ru.practicum.gateway.item.ItemClient;
import ru.practicum.gateway.item.ItemController;
import ru.practicum.gateway.item.dto.CommentShortDto;
import ru.practicum.gateway.item.dto.ItemDto;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = Gateway.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto validItemDto;
    private ItemDto invalidItemDto;
    private CommentShortDto validCommentDto;
    private CommentShortDto invalidCommentDto;

    private final long userId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        validItemDto = ItemDto.builder()
                .name("Дрель Bosch")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        invalidItemDto = ItemDto.builder()
                .name("  ")
                .description("")
                .available(null)
                .build();

        validCommentDto = CommentShortDto.builder()
                .text("Отличный инструмент, всё работает!")
                .build();

        invalidCommentDto = CommentShortDto.builder()
                .text("   ")
                .build();
    }


    @Test
    void getItems_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.getItems(userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.getItem(itemId, userId)).thenReturn(responseEntity);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }


    @Test
    void createItem_withValidDto_shouldReturnStatus200() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.createItem(eq(userId), any(ItemDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_withInvalidDto_shouldReturnStatus400BadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void updateItem_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.updateItem(any(ItemDto.class), eq(itemId), eq(userId))).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk());
    }


    @Test
    void searchItem_withBlankText_shouldReturnEmptyListImmediately() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "    ")) // Передаем пробелы [INDEX]
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(0));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void searchItem_withValidText_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.searchItem(userId, "дрель")).thenReturn(responseEntity);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }


    @Test
    void createComment_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(itemClient.createComment(eq(itemId), eq(userId), any(CommentShortDto.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_withInvalidDto_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest());
    }
}