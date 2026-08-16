package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import static ru.practicum.shareit.user.UserMapper.*;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUser(Long userId) {
        return toUserDto(userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userStorage.isEmail(userDto.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        User user = toUser(userDto);
        return toUserDto(userStorage.createUser(user));
    }

    @Override
    public UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (updateUserDto.getName() != null && !updateUserDto.getName().isBlank()) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isBlank()) {
            if (userStorage.isEmail(updateUserDto.getEmail())) {
                throw new EmailAlreadyTakenException("Email уже занят");
            }
            userStorage.removeEmail(userId);
            user.setEmail(updateUserDto.getEmail());
        }

        return toUpdateUserDto(userStorage.updateUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}


