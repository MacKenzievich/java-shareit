package ru.practicum.server.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItServer.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentShortDto commentShortDto;
    private final Long userId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Отвертка")
                .description("Шлицевая, аккумуляторная")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Отличный инструмент, выручил!")
                .authorName("Дмитрий")
                .created(LocalDateTime.now())
                .build();

        commentShortDto = CommentShortDto.builder()
                .text("Отличный инструмент, выручил!")
                .build();
    }

    @Test
    void createItem_shouldReturnStatus201AndItemDto() throws Exception {
        Mockito.when(itemService.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItem_shouldReturnStatus200AndItemDto() throws Exception {
        itemDto.setName("Супер Отвертка");
        Mockito.when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(userId)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Супер Отвертка"));
    }

    @Test
    void getById_shouldReturnStatus200AndItemDto() throws Exception {
        Mockito.when(itemService.getById(eq(itemId), eq(userId)))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId));
    }

    @Test
    void getAllItems_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(itemService.getAllItems(eq(userId)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId));
    }

    @Test
    void search_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "отвертка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Отвертка"));
    }

    @Test
    void createComment_shouldReturnStatus201AndCommentDto() throws Exception {
        Mockito.when(itemService.createComment(eq(itemId), eq(userId), any(CommentShortDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentShortDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}