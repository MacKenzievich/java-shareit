package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User getUser(Long userId) {
        return userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
    }

    @Override
    public User createUser(User user) {
        if (userStorage.isEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (userStorage.isEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        return userStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}


