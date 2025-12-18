package com.greeceri.store.controllers.admin;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.services.CloudinaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = cloudinaryService.uploadImage(file);

        Map<String, String> result = Map.of("url", imageUrl);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Image uploaded successfully", result));
    }
}
