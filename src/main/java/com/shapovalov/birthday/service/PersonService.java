package com.shapovalov.birthday.service;

import com.shapovalov.birthday.dto.PersonCreateDto;
import com.shapovalov.birthday.dto.PersonResponseDto;
import com.shapovalov.birthday.dto.PersonUpdateDto;
import com.shapovalov.birthday.entity.Person;
import com.shapovalov.birthday.exception.PersonNotFoundException;
import com.shapovalov.birthday.mapper.PersonMapper;
import com.shapovalov.birthday.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    // получаем список всех людей

    public List<PersonResponseDto> getAllPersons(){
        return personRepository.findAll().stream()
                .map(personMapper::toResponseDto)
                .toList();
    }

    // получаем человека по id
    public PersonResponseDto getPersonById(Long id){
        Person person = personRepository.findById(id)
                .orElseThrow(()-> new PersonNotFoundException("Person not found"));
        return personMapper.toResponseDto(person);
    }

    // получаем ближайшие др
    public List<PersonResponseDto> getNextBirthdays(int daysAhead) {
        return personRepository.findAll().stream()
                .map(personMapper::toResponseDto)
                .filter(dto -> dto.getDaysUntilBirthday() <= daysAhead)
                .sorted(Comparator.comparingInt(PersonResponseDto::getDaysUntilBirthday))
                .toList();
    }
    // добавление человека
    @Transactional
    public PersonResponseDto createPerson(PersonCreateDto dto) {
        Person person = personMapper.toEntity(dto);
        Person saved = personRepository.save(person);
        return personMapper.toResponseDto(saved);
    }
    // обновление человека
    public PersonResponseDto updatePerson(Long id, PersonUpdateDto dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(()-> new PersonNotFoundException("Person not found"));
        personMapper.updateEntityFromDto(dto, person);
        Person updated = personRepository.save(person);
        return personMapper.toResponseDto(updated);
    }

    // удалить человека
    @Transactional
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)){
            throw new PersonNotFoundException("Person not found");
        }
        personRepository.deleteById(id);
    }
}
