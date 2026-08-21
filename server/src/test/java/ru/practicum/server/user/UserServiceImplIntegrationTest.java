package ru.practicum.server.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void updateUser_shouldSuccessfullyUpdateOnlyPassedFieldsInDb() {
        User user = new User();
        user.setName("Старое Имя");
        user.setEmail("user_integration@mail.com");
        user = userRepository.save(user);

        UpdateUserDto updateDto = new UpdateUserDto(null, "Новое Имя", null);

        UpdateUserDto result = userService.updateUser(user.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Новое Имя");
        assertThat(result.getEmail()).isEqualTo("user_integration@mail.com");

        User dbUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.getName()).isEqualTo("Новое Имя");
        assertThat(dbUser.getEmail()).isEqualTo("user_integration@mail.com");
    }
}
