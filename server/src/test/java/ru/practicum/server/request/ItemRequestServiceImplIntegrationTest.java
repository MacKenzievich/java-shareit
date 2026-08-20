package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
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
                .name("Сварочный инвертор")
                .description("220V, маска в комплекте")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getAllByRequester(requester.getId());

        assertThat(result).hasSize(1);
        ItemRequestDto resultDto = result.get(0);
        assertThat(resultDto.getDescription()).isEqualTo("Нужен сварочный аппарат");
        assertThat(resultDto.getItems()).hasSize(1);
        assertThat(resultDto.getItems().get(0).getName()).isEqualTo("Сварочный инвертор");
    }
}