package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    Optional<User> getUser(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    boolean isEmail(String email);

    void removeEmail(Long userId);
}
