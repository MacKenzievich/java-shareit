package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Test
    void getAllByRequester_shouldReturnRequestsWithItemsCorrectlyMapped() {
        User requester = new User();
        requester.setName("Алексей");
        requester.setEmail("alex@mail.com");
        requester = userRepository.save(requester);

        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@mail.com");
        owner = userRepository.save(owner);

        ItemRequest request = ItemRequest.builder()
                .description("Нужен сварочный аппарат")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        request = itemRequestRepository.save(request);

        Item item = Item.builder()
                .name("Сварочный аппарат инверторный")
                .description("220V, маска в комплекте")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        item = itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getAllByRequester(requester.getId());

        assertThat(result).hasSize(1);
        ItemRequestDto resultDto = result.get(0);
        assertThat(resultDto.getDescription()).isEqualTo("Нужен сварочный аппарат");

        assertThat(resultDto.getItems()).hasSize(1);
        ItemShortDto itemDto = resultDto.getItems().get(0);

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo("Сварочный аппарат инверторный");
        assertThat(itemDto.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getAllByRequester_shouldSortRequestsByCreationTimeDesc() {
        User requester = new User();
        requester.setName("Заказчик");
        requester.setEmail("req_sort@mail.com");
        requester = userRepository.save(requester);

        ItemRequest oldRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Старый запрос")
                .requester(requester)
                .created(LocalDateTime.now().minusHours(1))
                .build());

        ItemRequest newRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Новый запрос")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequestDto> result = itemRequestService.getAllByRequester(requester.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(newRequest.getId());
        assertThat(result.get(0).getDescription()).isEqualTo("Новый запрос");

        assertThat(result.get(1).getId()).isEqualTo(oldRequest.getId());
        assertThat(result.get(1).getDescription()).isEqualTo("Старый запрос");
    }

    @Test
    void getAll_shouldExcludeOwnRequests() {
        User user1 = new User();
        user1.setName("Пользователь 1");
        user1.setEmail("user1@mail.com");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("Пользователь 2");
        user2.setEmail("user2@mail.com");
        user2 = userRepository.save(user2);

        ItemRequest requestFromUser1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Запрос от первого пользователя")
                .requester(user1)
                .created(LocalDateTime.now())
                .build());

        ItemRequest requestFromUser2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Запрос от второго пользователя")
                .requester(user2)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequestDto> resultForUser1 = itemRequestService.getAll(user1.getId());

        assertThat(resultForUser1).hasSize(1);
        assertThat(resultForUser1.get(0).getId()).isEqualTo(requestFromUser2.getId());
        assertThat(resultForUser1.get(0).getDescription()).isEqualTo("Запрос от второго пользователя");
    }
}
