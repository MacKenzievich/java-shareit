package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User getUser(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    boolean isEmail(String email);
}
