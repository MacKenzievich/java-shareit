package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userStorage.getUser(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден, невозможно создать Item"));
        Item item = toItem(itemDto);
        item.setOwner(user);
        return toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemStorage.getItemById(itemId)
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
        return toItemDto(itemStorage.updateItem(itemId, item));
    }


    @Override
    public ItemDto getById(Long itemId) {
        return toItemDto(itemStorage.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Item не найден")));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemDtoList;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<Item> items = itemStorage.searchByText(text);
        System.out.println(items);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemDtoList;
    }
}



