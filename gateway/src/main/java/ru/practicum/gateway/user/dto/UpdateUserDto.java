package ru.practicum.gateway.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
