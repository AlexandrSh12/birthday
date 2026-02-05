package com.shapovalov.birthday.service;

import com.shapovalov.birthday.dto.PersonCreateDto;
import com.shapovalov.birthday.dto.PersonResponseDto;
import com.shapovalov.birthday.dto.PersonUpdateDto;
import com.shapovalov.birthday.entity.Person;
import com.shapovalov.birthday.exception.PersonNotFoundException;
import com.shapovalov.birthday.mapper.PersonMapper;
import com.shapovalov.birthday.repository.PersonRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PersonRepository personRepository;
    private static final String UPLOAD_DIR  = "uploads";

    public void savePhoto(Long personId, MultipartFile file) throws IOException{
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Фото пустое или отсутствует");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Расширение фото не подходит");
        }

        Person person = personRepository.findById(personId)
                .orElseThrow(()-> new PersonNotFoundException("Человек не найден"));

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        if (person.getPhotoFileName() != null) {
            Path oldFile = uploadPath.resolve(person.getPhotoFileName());
            Files.deleteIfExists(oldFile);
        }

        // получаем расширение файла
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")){
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // сохраняем с правильным именем файла
        String fileName = personId + extension;
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        // сохраняем в бд
        person.setPhotoFileName(fileName);
        personRepository.save(person);
    }
        public byte[] getPhoto(Long personId) throws IOException {
        Person person = personRepository.findById(personId)
                .orElseThrow(()-> new PersonNotFoundException("Человек не найден"));
        if (person.getPhotoFileName() == null) {
            return null;
        }
        Path filePath = Paths.get(UPLOAD_DIR, person.getPhotoFileName());
        if (!Files.exists(filePath)) {
            return null;
        }
        return Files.readAllBytes(filePath);
        }

    public String getPhotoContentType(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Человек не найден"));

        if (person.getPhotoFileName() == null) {
            return null;
        }

        String fileName = person.getPhotoFileName();

        if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "image/jpeg"; // по умолчанию
        }
    }

}
