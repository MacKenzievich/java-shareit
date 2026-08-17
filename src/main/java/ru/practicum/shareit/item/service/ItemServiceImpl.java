package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    // private final ItemStorage itemStorage;
    // private final UserStorage userStorage;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден, невозможно создать Item"));
        Item item = toItem(itemDto);
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Вы не являетесь владельцем");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }


    @Override
    public ItemDto getById(Long itemId) {
        return toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item не найден")));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemDtoList;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!text.isBlank()) {
            Boolean available = true;
            List<Item> items = itemRepository.findByNameContainingIgnoreCaseAndAvailableOrDescriptionContainingIgnoreCaseAndAvailable(text, available, text, available);
            List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
            return itemDtoList;
        } else {
            return Collections.emptyList();
        }
    }
}



