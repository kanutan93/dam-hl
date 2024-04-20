package ru.hl.coreservice.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;

import java.util.List;

public interface ImageService {

  List<ImageResponseDto> getImages(String category);

  InputStreamResource downloadImage(Integer id);

  void uploadImage(MultipartFile file);

  void updateImageCategory(Integer id, String category, Double categoryMatchResult);

  void deleteImage(Integer id);
}
