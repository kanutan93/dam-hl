package ru.hl.coreservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hl.coreservice.aop.ReadOnlyConnection;
import ru.hl.coreservice.kafka.payload.ImagePayload;
import ru.hl.coreservice.mapper.image.ImageMapper;
import ru.hl.coreservice.model.dao.ImageDao;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;
import ru.hl.coreservice.repository.ImageRepository;
import ru.hl.coreservice.service.ImageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static liquibase.repackaged.org.apache.commons.collections4.CollectionUtils.isNotEmpty;

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

  @Value("${kafka.adding-new-image-topic}")
  private String addingNewImageTopic;

  @Value("${image-directory}")
  private String imageDirectory;

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
  public List<String> getCategories() {
    log.info("Trying to get available categories...");

    List<String> categories = imageRepository.getCategories();

    log.info("{} categories have been receeived succesfully", categories.size());
    return categories;
  }

  @Override
  @ReadOnlyConnection
  @Transactional(readOnly = true)
  public ImageResponseDto getImage(Integer id) {
    log.info("Trying to get image by id: {}", id);

    ImageDao imageDao = imageRepository.getImageById(id);
    ImageResponseDto image = imageMapper.toImageResponseDto(imageDao);

    log.info("Image was successfully received by id: {}", id);

    return image;
  }

  @Override
  @SneakyThrows
  @ReadOnlyConnection
  @Transactional(readOnly = true)
  public InputStream downloadImage(Integer id) {
    log.info("Trying to download image with id: {}", id);

    ImageDao image = imageRepository.getImageById(id);
    File file = new File(imageDirectory + "/" + image.getFilename());
    FileInputStream inputStream = new FileInputStream(file);

    log.info("Image with id: {} has been successfully downloaded", id);

    return inputStream;
  }

  @Override
  @Transactional
  @SneakyThrows
  public void uploadImage(MultipartFile file) {
    String filename = file.getOriginalFilename();
    log.info("Trying to upload image with filename: {}", filename);

    int id = imageRepository.createImage(filename);
    try (InputStream inputStream = file.getInputStream();) {
      File dest = new File(imageDirectory + "/" + filename);
      FileUtils.copyInputStreamToFile(inputStream, dest);
    }
    ImagePayload imagePayload = new ImagePayload(id, filename);
    kafkaTemplate.send(addingNewImageTopic, objectMapper.writeValueAsString(imagePayload));

    log.info("Image with id: {}, filename: {} was successfully uploaded", id, filename);
  }

  @Override
  @Transactional
  public void updateImageCategory(Integer id, String category, Double categoryMatchResult) {
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
