package ru.practicum.server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDto dto = new UserDto(1L, "Александр", "alex@mail.com");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Александр");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alex@mail.com");
    }

    @Test
    void testUserDtoDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Александр\",\"email\":\"alex@mail.com\"}";

        UserDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Александр");
        assertThat(result.getEmail()).isEqualTo("alex@mail.com");
    }
}