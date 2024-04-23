package ru.hl.coreservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.coreservice.controller.ImageController;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;
import ru.hl.coreservice.service.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageControllerImpl implements ImageController {

  private final ImageService imageService;

  @Override
  public ResponseEntity<List<ImageResponseDto>> getImages(String category) {
    List<ImageResponseDto> images = imageService.getImages(category);
    return ResponseEntity.ok(images);
  }

  @Override
  public ResponseEntity<List<String>> getCategories() {
    List<String> categories = imageService.getCategories();
    return ResponseEntity.ok(categories);
  }

  @Override
  @SneakyThrows
  public ResponseEntity<InputStreamResource> downloadImage(Integer id) {
    ImageResponseDto image = imageService.getImage(id);
    var is = imageService.downloadImage(id);
    HttpHeaders respHeaders = new HttpHeaders();
    respHeaders.setContentType(MediaType.IMAGE_JPEG);
    respHeaders.setContentDispositionFormData("attachment", image.getFilename());
    return new ResponseEntity<>(new InputStreamResource(is), respHeaders, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> uploadImage(MultipartFile file) {
    imageService.uploadImage(file);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteImage(Integer id) {
    imageService.deleteImage(id);
    return ResponseEntity.ok().build();
  }
}
