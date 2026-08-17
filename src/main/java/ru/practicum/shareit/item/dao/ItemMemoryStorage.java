package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemMemoryStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    public long itemsId = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> findAllByOwnerId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null)
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchByText(String text) {

        String query = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(query))
                                || (item.getDescription() != null && item.getDescription().toLowerCase().contains(query))
                )
                .toList();
    }


    private Long getNextId() {
        return ++itemsId;
    }
}
