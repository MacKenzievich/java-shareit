package ru.practicum.booking;


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
import ru.practicum.gateway.booking.BookingClient;
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.booking.dto.BookItemRequestDto;
import ru.practicum.gateway.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = Gateway.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookItemRequestDto validBookingDto;
    private final long userId = 1L;
    private final Long bookingId = 1L;

    @BeforeEach
    void setUp() {
        validBookingDto = new BookItemRequestDto();
        validBookingDto.setItemId(1L);
        validBookingDto.setStart(LocalDateTime.now().plusDays(1));
        validBookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }


    @Test
    void getAll_withValidState_shouldReturnStatus200() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(bookingClient.getBookings(eq(userId), eq(BookingState.ALL)))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_withUnknownState_shouldReturnStatus400BadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "UNSUPPORTED_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwner_withValidPagination_shouldReturnStatus200() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(bookingClient.getBookingCurrentOwner(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwner_withNegativeFrom_shouldReturnStatus400BadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwner_withZeroSize_shouldReturnStatus400BadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_withValidDto_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void bookItem_missingUserHeader_shouldReturnStatus400BadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getBooking_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(bookingClient.getBooking(eq(userId), eq(bookingId)))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void approveStatus_shouldForwardToClient() throws Exception {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(bookingClient.approveStatus(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(responseEntity);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }
}