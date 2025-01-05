package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PasswordMapper extends RowMapperStrategy<String> {
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("password");
    }
}
