package lms.pixel.backend.utils;

import org.springframework.jdbc.core.RowMapper;

import lms.pixel.backend.model.myFile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileRowMapper implements RowMapper<myFile> {
    @Override
    public myFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        myFile file = new myFile();
        file.setFileid(rs.getInt("fileid"));
        file.setPath(rs.getString("path"));
        file.setUserid(rs.getInt("userid"));
        file.setTimestamp(rs.getTimestamp("timestamp"));
        file.setTaskid(rs.getInt("taskid"));
        file.setCourseid(rs.getInt("courseid"));
        file.setMessageid(rs.getInt("messageid"));
        file.setProfileid(rs.getInt("profileid"));
        return file;
    }

}
