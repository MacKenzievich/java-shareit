package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.user.dto.UserMapper.*;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    // private final UserStorage userStorage;
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long userId) {
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }
        User user = toUser(userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    public UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (updateUserDto.getName() != null && !updateUserDto.getName().isBlank()) {
            user.setName(updateUserDto.getName());
        }
        String newEmail = updateUserDto.getEmail();
        String oldEmail = user.getEmail();
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(oldEmail)) {
                if (existsByEmail(newEmail)) {
                    throw new EmailAlreadyTakenException("email уже занят!");
                }
                user.setEmail(updateUserDto.getEmail());
            }
        }
        return toUpdateUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}


