package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUser(Long userId);

    UserDto createUser(UserDto userDto);

    UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto);

    void deleteUser(Long userId);
}
