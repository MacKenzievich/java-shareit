package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;


import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Optional<Item> getItemById(Long id);

    Item updateItem(Long itemId, Item item);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> searchByText(String text);
}
