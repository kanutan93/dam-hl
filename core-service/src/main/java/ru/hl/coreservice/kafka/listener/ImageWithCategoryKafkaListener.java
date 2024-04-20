package ru.hl.coreservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.hl.coreservice.kafka.payload.ImageWithCategoryPayload;
import ru.hl.coreservice.service.ImageService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageWithCategoryKafkaListener {

  private final ImageService imageService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.saving-new-image-category-topic}")
  @SneakyThrows
  public void consume(ConsumerRecord<String, String> record) {
    String payload = record.value();
    log.info("New message received: {}", payload);

    ImageWithCategoryPayload imageWithCategoryPayload = objectMapper.readValue(payload, ImageWithCategoryPayload.class);
    Integer id = imageWithCategoryPayload.getId();
    String category = imageWithCategoryPayload.getCategory();
    Double categoryMatchResult = imageWithCategoryPayload.getCategoryMatchResult();
    ImageWithCategoryPayload.Action action = imageWithCategoryPayload.getAction();

    switch (action)  {
      case UPDATE:
        imageService.updateImageCategory(id, category, categoryMatchResult);
        break;
      case DELETE:
        imageService.deleteImage(id);
    }

    log.info("Message has been received successfully");
  }
}
