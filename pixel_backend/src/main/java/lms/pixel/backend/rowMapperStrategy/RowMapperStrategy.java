package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public abstract class RowMapperStrategy<T> implements RowMapper<T> {
    public abstract T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
