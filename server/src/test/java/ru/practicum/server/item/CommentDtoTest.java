package ru.practicum.server.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDtoSerialization() throws Exception {
        LocalDateTime createdTime = LocalDateTime.of(2026, 8, 20, 16, 0, 0);

        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Тестовый комментарий")
                .authorName("Алексей")
                .created(createdTime)
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-08-20T16:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Тестовый комментарий");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Алексей");
    }
}