package ru.hl.coreservice.mapper.post;

import org.mapstruct.Mapper;
import ru.hl.coreservice.kafka.payload.ImageWithCategoryPayload;
import ru.hl.coreservice.model.dao.ImageDao;
import ru.hl.coreservice.model.dto.response.ImageResponseDto;

@Mapper
public interface ImageMapper {

  ImageResponseDto toImageResponseDto(ImageDao imageDao);

  ImageWithCategoryPayload toPostPayload(Integer receiverUserId, ImageWithCategoryPayload.Action action, ImageResponseDto imageResponseDto);
}
