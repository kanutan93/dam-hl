package ru.hl.primaryservice.model.dao;

import lombok.Data;

@Data
public class ImageDao {
  private Integer id;
  private String filename;
  private Integer category;
  private Double categoryMatchResult;
}
