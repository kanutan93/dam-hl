package ru.hl.coreservice.kafka.payload;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ImagePayload {

  private final Integer id;
  private final String filename;
  private String category;
  private Double categoryMatchResult;
  private Action action;


  public enum Action {
    UPDATE,
    DELETE
  }
}
