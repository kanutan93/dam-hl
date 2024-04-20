package ru.hl.coreservice.kafka.payload;

import lombok.Data;

@Data
public class ImageWithCategoryPayload {

  private Integer id;
  private String category;
  private Double categoryMatchResult;
  private Action action;


  public enum Action {
    UPDATE,
    DELETE
  }
}
