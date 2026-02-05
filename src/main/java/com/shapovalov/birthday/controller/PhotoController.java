package com.shapovalov.birthday.controller;

import com.shapovalov.birthday.service.PersonService;
import com.shapovalov.birthday.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/{personId}")
    public ResponseEntity <String> uploadPhoto(
            @PathVariable Long personId,
            @RequestParam("file")MultipartFile file) {

        try {
            photoService.savePhoto(personId, file);
            return ResponseEntity.ok("Фото успешно загружено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке фото" + e.getMessage());
        }
    }
    @GetMapping("/{personId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long personId) {

        try {
            byte[] photo = photoService.getPhoto(personId);

            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = photoService.getPhotoContentType(personId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(photo);
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
