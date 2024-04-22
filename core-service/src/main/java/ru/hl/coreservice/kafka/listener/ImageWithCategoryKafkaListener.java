package ru.hl.coreservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.hl.coreservice.kafka.payload.ImagePayload;
import ru.hl.coreservice.mapper.image.ImageMapper;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;
import ru.hl.coreservice.service.ImageService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageWithCategoryKafkaListener {

  private static final String IMAGES_CACHE = "imagesCache";
  private final ImageService imageService;
  private final ImageMapper imageMapper;
  private final ObjectMapper objectMapper;
  private final CacheManager cacheManager;

  @KafkaListener(topics = "${kafka.saving-new-image-category-topic}")
  @SneakyThrows
  public void consume(ConsumerRecord<String, String> record) {
    String payload = record.value();
    log.info("New message received: {}", payload);

    var imagePayload = objectMapper.readValue(payload, ImagePayload.class);
    Integer id = imagePayload.getId();
    String category = imagePayload.getCategory();
    Double categoryMatchResult = imagePayload.getCategoryMatchResult();
    ImagePayload.Action action = imagePayload.getAction();

    switch (action)  {
      case UPDATE:
        imageService.updateImageCategory(id, category, categoryMatchResult);
        saveImageToCache(imageMapper.toImageResponseDto(imagePayload));
        break;
      case DELETE:
        imageService.deleteImage(imagePayload.getId());
    }

    log.info("Message has been received successfully");
  }

  private void saveImageToCache(ImageResponseDto image) {
    Cache cache = cacheManager.getCache(IMAGES_CACHE);
    List<ImageResponseDto> imagesFromCache = Optional.ofNullable(cache)
        .map(it -> it.get(image.getCategory(), List.class))
        .orElse(null);
    if (imagesFromCache != null) {
      var images = new LinkedList<>(imagesFromCache);
      images.addFirst(image);
      cache.put(image.getCategory(), images);
    }
  }
}
