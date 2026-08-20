package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}
