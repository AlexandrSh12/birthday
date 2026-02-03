package com.shapovalov.birthday.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponseDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String photoUrl;
    private Integer daysUntilBirthday;
    private Integer age;
}
