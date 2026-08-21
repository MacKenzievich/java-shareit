package ru.practicum.server.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ShareItServer.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoShort itemRequestDtoShort;
    private final Long userId = 1L;
    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestDtoShort = ItemRequestDtoShort.builder()
                .description("Нужна стремянка для ремонта")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужна стремянка для ремонта")
                .requesterId(userId)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void create_shouldReturnStatus200AndItemRequestDto() throws Exception {
        Mockito.when(itemRequestService.create(eq(userId), any(ItemRequestDtoShort.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDtoShort)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(itemRequestDto.getRequesterId()));
    }

    @Test
    void getById_shouldReturnStatus200AndItemRequestDto() throws Exception {
        Mockito.when(itemRequestService.getById(eq(userId), eq(requestId)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна стремянка для ремонта"));
    }

    @Test
    void getAllByRequester_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(itemRequestService.getAllByRequester(eq(userId)))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Нужна стремянка для ремонта"));
    }


    @Test
    void getAll_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(itemRequestService.getAll(eq(userId)))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Нужна стремянка для ремонта"));
    }
}
