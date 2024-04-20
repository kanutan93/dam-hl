package ru.hl.primaryservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.primaryservice.aop.ReadOnlyConnection;
import ru.hl.primaryservice.mapper.post.ImageMapper;
import ru.hl.primaryservice.model.dao.ImageDao;
import ru.hl.primaryservice.model.dto.response.ImageResponseDto;
import ru.hl.primaryservice.repository.ImageRepository;
import ru.hl.primaryservice.service.ImageService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private static final String IMAGES_CACHE = "imagesCache";
  private final ImageRepository imageRepository;
  private final ImageMapper imageMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final CacheManager cacheManager;

  @Value("${spring.kafka.topic}")
  private String kafkaTopic;

  @Override
  @ReadOnlyConnection
  @Transactional(readOnly = true)
  public List<ImageResponseDto> getImages(String category) {
    log.info("Trying to get images by category: {}", category);

    Cache cache = cacheManager.getCache(IMAGES_CACHE);
    List<ImageResponseDto> imagesFromCache = Optional.ofNullable(cache)
        .filter(it -> category != null)
        .map(it -> it.get(category, List.class))
        .orElse(null);
    if (imagesFromCache != null) {
      log.info("Images was successfully received from cache by category: {}", category);
      return imagesFromCache;
    } else {
      List<ImageDao> imageDaoList = StringUtils.isNotEmpty(category)
          ? imageRepository.getImagesByCategory(category)
          : imageRepository.getImages();
      List<ImageResponseDto> images = imageDaoList.stream()
          .map(imageMapper::toImageResponseDto)
          .collect(Collectors.toList());
      cache.put(category, images);

      log.info("Images was successfully received by category: {}", category);

      return images;
    }
  }

  @Override
  @SneakyThrows
  @Transactional
  public InputStreamResource downloadImage(Integer id) {
    log.info("Trying to download image with id: {}", id);

    ImageDao image = imageRepository.getImageById(id);
    //TODO get InputStream for file

    log.info("Image with id: {} has been successfully downloaded", id);

    return new InputStreamResource(null);
  }

  @Override
  @Transactional
  public void uploadImage(MultipartFile file) {
    String filename = file.getOriginalFilename();
    log.info("Trying to upload image with filename: {}", filename);

    imageRepository.createImage(filename);
    //TODO saving image

    log.info("Image with filename: {} was successfully uploaded", filename);
  }

  @Override
  @Transactional
  public void saveImageCategory(Integer id, String category, Double categoryMatchResult) {
    log.info("Trying to update image with id: {}, category: {}, categoryMatchResult: {}", id, category, categoryMatchResult);

    imageRepository.updateImage(id, category, categoryMatchResult);

    log.info("Image with with id: {} was successfully updated with category: {}, categoryMatchResult: {}", id, category, categoryMatchResult);
  }

  @Override
  @Transactional
  public void deleteImage(Integer id) {
    log.info("Trying to delete image with id: {}", id);

    imageRepository.deleteImage(id);

    log.info("Image with id: {} was successfully deleted", id);
  }
}
