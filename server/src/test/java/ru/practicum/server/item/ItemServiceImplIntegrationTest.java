package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void getAllItems_shouldReturnAllUserItemsFromDatabase() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner_item@mail.com");
        owner = userRepository.save(owner);

        Item item1 = Item.builder()
                .name("Дрель")
                .description("Электрическая ударная")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Лестница")
                .description("Стремянка 3 метра")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item2);

        List<ItemDto> result = itemService.getAllItems(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemDto::getName).containsExactlyInAnyOrder("Дрель", "Лестница");
    }
}