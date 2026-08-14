package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

public interface UserService {

    User getUser(Long userId);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
