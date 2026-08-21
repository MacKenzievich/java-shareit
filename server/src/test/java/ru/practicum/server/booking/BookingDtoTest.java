package ru.practicum.server.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 12, 25, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 26, 18, 30, 0);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(new BookingDto.Booker(2L, "John"))
                .item(new BookingDto.Item(3L, "Drill"))
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-12-25T10:00:00");

        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-12-26T18:30:00");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
