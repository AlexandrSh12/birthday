package com.shapovalov.birthday.controller;

import com.shapovalov.birthday.dto.PersonCreateDto;
import com.shapovalov.birthday.dto.PersonResponseDto;
import com.shapovalov.birthday.dto.PersonUpdateDto;
import com.shapovalov.birthday.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    // GET /api/persons - получить все др
    @GetMapping
    public List<PersonResponseDto> getAllPersons(){
        return personService.getAllPersons();
    }

    // GET /api/persons/upcoming - получить ближайшие др
    @GetMapping("/next")
    public List<PersonResponseDto> getNextBirthdays() {
        return personService.getNextBirthdays(30);
    }

    // GET /api/persons/{id} получить др по id
    @GetMapping("/{id}")
    public PersonResponseDto getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id);
    }

    // POST /api/persons - создать человека
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponseDto createPerson(@Valid @RequestBody PersonCreateDto dto){
        return personService.createPerson(dto);
    }

    // PUT /api/persons/{id} - обновить человека
    @PutMapping("/{id}")
    public PersonResponseDto updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonUpdateDto dto) {
        return personService.updatePerson(id, dto);
    }

    // DELETE /api/persons/{id} - удалить человека
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
    }
}
