package ru.hl.coreservice.kafka.payload;

import lombok.Data;

@Data
public class ImageWithCategoryPayload {

  private Integer id;
  private String category;
  private Double categoryMatchResult;
}
