package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public User getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        if (userStorage.isEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        return userStorage.createUser(user);
    }

    public User updateUser(Long userId, User user) {
        if (userStorage.isEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        return userStorage.updateUser(userId, user);
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
