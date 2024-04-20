package ru.hl.coreservice.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImagePayload {

  private Integer id;
  private String filename;
}
