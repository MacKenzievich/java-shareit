package ru.practicum.server.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItServer.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingShortDto bookingShortDto;
    private final Long userId = 1L;
    private final Long bookingId = 1L;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        bookingShortDto = BookingShortDto.builder()
                .itemId(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(new BookingDto.Booker(userId, "BookerName"))
                .item(new BookingDto.Item(1L, "ItemName"))
                .build();
    }

    @Test
    void create_shouldReturnStatus201AndBookingDto() throws Exception {
        Mockito.when(bookingService.create(any(BookingShortDto.class), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()));
    }

    @Test
    void approve_shouldReturnStatus200AndBookingDto() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.approve(eq(bookingId), eq(userId), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_shouldReturnStatus200AndBookingDto() throws Exception {
        Mockito.when(bookingService.getById(eq(bookingId), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getAllByUser_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(bookingService.getAllByUser(eq(userId), anyString()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }

    @Test
    void getAllByOwner_shouldReturnStatus200AndList() throws Exception {
        Mockito.when(bookingService.getAllByOwner(eq(userId), anyString()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }
}