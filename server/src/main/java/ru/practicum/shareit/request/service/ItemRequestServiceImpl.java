package ru.practicum.shareit.request.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDtoShort itemRequestDtoShort) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoShort);

        itemRequest.setRequester(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User не найден!")));

        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User не найден!");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request не найден!"));
        List<ItemShortDto> items = itemRepository.findByRequest_IdOrderById(requestId).stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }


    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User не найден!");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        return getItemRequestsDtoWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllByRequester(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User не найден!");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        return getItemRequestsDtoWithItems(requests);
    }

    private List<ItemRequestDto> getItemRequestsDtoWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequest_IdIn(requestsId);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.toList()
                ));

        List<ItemRequestDto> itemRequestDto = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            List<Item> itemsTemp = itemsByRequestId.getOrDefault(itemRequest.getId(), List.of());
            itemRequestDto.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsTemp));
        }

        return itemRequestDto;
    }
}