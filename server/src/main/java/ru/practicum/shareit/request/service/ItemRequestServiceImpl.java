package ru.practicum.shareit.request.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        return null;
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDtoShort) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public List<ItemRequestDto> getAllByRequester(Long userId, Integer from, Integer size) {
        return List.of();
    }
}
