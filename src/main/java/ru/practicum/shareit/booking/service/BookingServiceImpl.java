package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    public BookingDto create(BookingShortDto bookingShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingShortDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item не найден"));
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("User вещи не может забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь неактивна");
        }
        Booking booking = toBooking(bookingShortDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd() == booking.getStart()) {
            throw new ValidationException("невозможно забронировать вещь! ");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking не найден"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new RuntimeException("Вы не являетесь владельцем вещи");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new RuntimeException("Бронирование уже подтверждено или отклонено");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User не найден"));
        List<Booking> bookingDtoList;
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByItemOwner(user);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByItemOwnerAndEndBefore(user,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now());
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, REJECTED);
                break;
            default:
                throw new RuntimeException("Неподдерживаемый статус");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User не найден"));
        List<Booking> bookingDtoList;
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByBooker(user);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now());
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByBookerAndStatusEquals(user, WAITING);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByBookerAndStatusEquals(user, REJECTED);
                break;
            default:
                throw new RuntimeException("Неподдерживаемый статус");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new UserNotFoundException("Только владелец вещи и арендатор может просмотреть");
        }

        return toBookingDto(booking);
    }
}
