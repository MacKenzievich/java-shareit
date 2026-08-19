package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    // private final ItemStorage itemStorage;
    // private final UserStorage userStorage;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public ItemDto getById(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item не найден"));
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(toItemDto(item));
        if (item.getOwner().getId().equals(ownerId)) {
            List<Long> idItems = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
            getAllBookingsByItem(itemDtoList, idItems);
        }
        ItemDto itemDto = itemDtoList.get(0);
        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<Long> idItems = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        getAllBookingsByItem(itemDtoList, idItems);

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIdIn(idItems, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        itemDtoList.forEach(itemDto ->
                itemDto.setComments(comments.getOrDefault(itemDto.getId(), List.of()))
        );
        itemDtoList.sort(comparing(ItemDto::getId));

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

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentShortDto commentShortDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item не найден"));
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now())) {
            throw new ValidationException("Вы не можете оставить комментарий: бронирование еще не завершилось или отсутствует.");
        }
        Comment comment = toComment(commentShortDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now());
        commentRepository.save(comment);

        return toCommentDto(comment);
    }

    private void getAllBookingsByItem(List<ItemDto> itemDtoList, List<Long> idItems) {
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<BookingForItemDto>> lastBookings = bookingRepository.findAllByItemIdInAndStartLessThanEqualAndStatus(
                        idItems, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::toBookingForItemDto)
                .collect(Collectors.groupingBy(BookingForItemDto::getItemId));

        Map<Long, List<BookingForItemDto>> nextBookings = bookingRepository.findAllByItemIdInAndStartAfterAndStatus(
                        idItems, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(BookingMapper::toBookingForItemDto)
                .collect(Collectors.groupingBy(BookingForItemDto::getItemId));

        itemDtoList.forEach(itemDto -> {
            Long itemId = itemDto.getId();

            BookingForItemDto lastBooking = lastBookings.getOrDefault(itemId, List.of()).stream()
                    .findFirst()
                    .orElse(null);

            BookingForItemDto nextBooking = nextBookings.getOrDefault(itemId, List.of()).stream()
                    .findFirst()
                    .orElse(null);

            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        });
    }


}



