package ru.practicum.gateway.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
}

