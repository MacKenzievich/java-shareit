package ru.practicum.gateway.booking.dto;

import java.util.Optional;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static Optional<BookingStatus> from(String stringStatus) {
        for (BookingStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
