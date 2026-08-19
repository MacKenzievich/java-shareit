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

    List<Booking> findAllByBooker(User booker);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start);

    List<Booking> findAllByBookerAndStatusEquals(User booker, BookingStatus status);

    List<Booking> findAllByItemOwner(User owner);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status);

    Optional<Booking> findFirstByItemIdInAndStartAfterAndStatus(List<Long> idItems, LocalDateTime now,
                                                                BookingStatus approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartLessThanEqualAndStatus(List<Long> idItems, LocalDateTime now,
                                                                        BookingStatus approved, Sort sort);


    @Query("select count(b.id) as count " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < ?3 " +
            "group by b.id ")
    Long isFindBooking(Long itemId, Long userId, LocalDateTime end);

}



