package ru.hl.coreservice.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.hl.coreservice.mapper.image.ImageDaoRowMapper;
import ru.hl.coreservice.model.dao.ImageDao;
import ru.hl.coreservice.repository.ImageRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<ImageDao> getImages() {
    return jdbcTemplate.query("SELECT * FROM images " +
            "WHERE category IS NOT NULL " +
            "ORDER BY id DESC " +
            "LIMIT 1000",
        new ImageDaoRowMapper());
  }

  @Override
  public List<String> getCategories() {
    return jdbcTemplate.query("SELECT DISTINCT(category) FROM images", (rs, rowNum) -> rs.getString("category"));
  }

  @Override
  public List<ImageDao> getImagesByCategory(String category) {
    return jdbcTemplate.query("SELECT * FROM images " +
            "WHERE category = ? " +
            "ORDER BY id DESC " +
            "LIMIT 1000",
        new ImageDaoRowMapper(),
        category);
  }

  @Override
  public ImageDao getImageById(Integer id) {
    return jdbcTemplate.queryForObject("SELECT * FROM images " +
            "WHERE id = ?",
        new ImageDaoRowMapper(),
        id);
  }

  @Override
  public int createImage(String filename) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO images (filename) " +
              "VALUES (?)",
          Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, filename);
      return preparedStatement;
    }, generatedKeyHolder);

    return (int) generatedKeyHolder.getKeys().get("id");
  }

  @Override
  public void updateImage(Integer id, String category, Double categoryMatchResult) {
    jdbcTemplate.update("UPDATE images " +
            "SET category = ?, category_match_result = ? " +
            "WHERE id = ?",
        category,categoryMatchResult, id);
  }

  @Override
  public void deleteImage(Integer id) {
    jdbcTemplate.update("DELETE FROM images " +
            "WHERE id = ?",
        id);
  }
}
