package ru.hl.coreservice.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;

import java.util.List;

@RequestMapping("/api/image")
public interface ImageController {

  @GetMapping("/list")
  ResponseEntity<List<ImageResponseDto>> getImages(@RequestParam String category);

  @GetMapping("/download/{id}")
  ResponseEntity<InputStreamResource> downloadImage(@PathVariable Integer id);

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteImage(@PathVariable Integer id);

  @PostMapping("/upload")
  ResponseEntity<Void> uploadImage(@RequestParam("file") MultipartFile file);
}
