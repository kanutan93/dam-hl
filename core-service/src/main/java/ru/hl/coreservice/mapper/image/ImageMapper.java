package ru.hl.coreservice.mapper.image;

import org.mapstruct.Mapper;
import ru.hl.coreservice.kafka.payload.ImagePayload;
import ru.hl.coreservice.model.dao.ImageDao;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;

@Mapper
public interface ImageMapper {

  ImageResponseDto toImageResponseDto(ImageDao imageDao);

  ImageResponseDto toImageResponseDto(ImagePayload imagePayload);
}
