package ru.hl.coreservice.mapper.image;

import org.springframework.jdbc.core.RowMapper;
import ru.hl.coreservice.model.dao.ImageDao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageDaoRowMapper implements RowMapper<ImageDao> {

    @Override
    public ImageDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        var imageDao = new ImageDao();

        imageDao.setId(rs.getInt("id"));
        imageDao.setFilename(rs.getString("filename"));
        imageDao.setCategory(rs.getInt("category"));
        imageDao.setCategoryMatchResult(rs.getDouble("category_match_result"));

        return imageDao;
    }
}
