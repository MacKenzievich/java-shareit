package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemRequestDtoShort {
    private Long id;

    private String description;

    private Long requesterId;

    private LocalDateTime created;
}