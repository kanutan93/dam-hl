package ru.hl.primaryservice.repository;


import ru.hl.primaryservice.model.dao.ImageDao;

import java.util.List;

public interface ImageRepository {

    List<ImageDao> getImages();
    List<ImageDao> getImagesByCategory(String category);
    ImageDao getImageById(Integer id);
    int createImage(String filename);
    void updateImage(Integer id, String category, Double categoryMatchResult);
    void deleteImage(Integer id);
}
