package ru.hl.primaryservice.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.primaryservice.model.dto.response.ImageResponseDto;

import java.io.InputStream;
import java.util.List;

public interface ImageService {

  List<ImageResponseDto> getImages(String category);

  InputStreamResource downloadImage(Integer id);

  void uploadImage(MultipartFile file);

  void saveImageCategory(Integer id, String category, Double categoryMatchResult);

  void deleteImage(Integer id);
}
