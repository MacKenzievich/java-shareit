package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getById(Long itemId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> search(String text);
}
