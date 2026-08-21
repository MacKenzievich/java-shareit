package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UpdateUserDto;
import ru.practicum.gateway.user.dto.UserRequestDto;
import ru.practicum.gateway.util.Create;

@Controller
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Getting a User with Id: {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(Create.class) UserRequestDto requestDto) {
        log.info("Adding a User: {}", requestDto);
        return userClient.createUser(requestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto,
                                             @PathVariable("userId") Long userId) {
        log.info("Gateway: Updating user id={}, dto={}", userId, updateUserDto);
        return userClient.updateUser(userId, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Delete a User: {}", id);
        return userClient.deleteUser(id);
    }
}
