package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Repository
public class UserMemoryStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    public long usersId = 0;

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)){
            throw new UserNotFoundException("Пользователь не найден");
        }
        return users.get(userId);
    }

    @Override
    public User updateUser(Long userId, User user) {
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isEmail(String email) {
        return emails.contains(email);
    }

    private Long getNextId() {
        return ++usersId;
    }
}
