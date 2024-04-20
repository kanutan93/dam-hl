package ru.hl.primaryservice.model.dto.response;

import lombok.Data;

@Data
public class ImageResponseDto {
    private Integer id;
    private String filename;
    private String category;
    private Double categoryMatchResult;
}
