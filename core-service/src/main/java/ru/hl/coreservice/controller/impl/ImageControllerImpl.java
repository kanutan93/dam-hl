package ru.hl.primaryservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.primaryservice.controller.ImageController;
import ru.hl.primaryservice.model.dto.response.ImageResponseDto;
import ru.hl.primaryservice.service.ImageService;

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
  @SneakyThrows
  public ResponseEntity<InputStreamResource> downloadImage(Integer id) {
    InputStreamResource isr = imageService.downloadImage(id);
    HttpHeaders respHeaders = new HttpHeaders();
    //TODO save content type to db and use it here
    respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    respHeaders.setContentLength(isr.contentLength());
    respHeaders.setContentDispositionFormData("attachment", isr.getFilename());
    return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
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
