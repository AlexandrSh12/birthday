package com.shapovalov.birthday.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PersonUpdateDto {
    @NotBlank(message = "Name can not be empty")
    private String name;

    @NotNull
    private LocalDate birthDate;
}
