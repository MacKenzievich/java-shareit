package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc (User booker);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime start,
                                                           LocalDateTime end);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime start);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User booker, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime start,
                                                              LocalDateTime end);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime start);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByStartDesc(User owner, BookingStatus status);

    List<Booking> findAllByItemIdInAndStartAfterAndStatus(List<Long> itemIds, LocalDateTime now,
                                                          BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndStartLessThanEqualAndStatus(List<Long> itemIds, LocalDateTime now,
                                                                  BookingStatus status, Sort sort);


    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime end
    );

}



