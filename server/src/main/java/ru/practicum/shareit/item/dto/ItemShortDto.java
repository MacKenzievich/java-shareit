package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
public class ItemShortDto {
    private Long id;

    private String name;

    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;

    private Long ownerId;
}