package ru.hl.coreservice.repository;


import ru.hl.coreservice.model.dao.ImageDao;

import java.util.List;

public interface ImageRepository {

    List<ImageDao> getImages();
    List<String> getCategories();
    List<ImageDao> getImagesByCategory(String category);
    ImageDao getImageById(Integer id);
    int createImage(String filename);
    void updateImage(Integer id, String category, Double categoryMatchResult);
    void deleteImage(Integer id);
}
