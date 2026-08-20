package ru.practicum.server.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        LocalDateTime createdTime = LocalDateTime.of(2026, 8, 20, 14, 30, 0);

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Хочу взять в аренду перфоратор")
                .requesterId(2L)
                .created(createdTime)
                .items(Collections.emptyList())
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-08-20T14:30:00");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Хочу взять в аренду перфоратор");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(2);
    }
}