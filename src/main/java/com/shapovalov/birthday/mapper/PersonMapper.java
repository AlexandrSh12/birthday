package com.shapovalov.birthday.mapper;
import com.shapovalov.birthday.dto.PersonCreateDto;
import com.shapovalov.birthday.dto.PersonResponseDto;
import com.shapovalov.birthday.dto.PersonUpdateDto;
import com.shapovalov.birthday.entity.Person;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    @Mapping(target = "photoUrl", source = "person", qualifiedByName = "mapPhotoUrl")
    @Mapping(target = "daysUntilBirthday", source = "birthDate", qualifiedByName = "calculateDaysUntilBirthday")
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAge")
    PersonResponseDto toResponseDto (Person person);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoFileName", ignore = true)
    Person toEntity(PersonCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoFileName", ignore = true)
    void updateEntityFromDto(PersonUpdateDto dto, @MappingTarget Person person);

    @Named("mapPhotoUrl")
    default String mapPhotoUrl(Person person) {
        if (person.getPhotoFileName() != null && person.getId() != null) {
            return "/api/photos/" + person.getId();
        }
        return null;
    }
    @Named("calculateDaysUntilBirthday")
    default Integer calculateDaysUntilBirthday(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = birthDate.withYear(today.getYear());

        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        return (int) ChronoUnit.DAYS.between(today, nextBirthday);
    }

    @Named("calculateAge")
    default Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }

        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}

