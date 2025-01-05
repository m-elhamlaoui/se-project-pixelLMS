package lms.pixel.backend.rowMapperStrategy;

import lms.pixel.backend.model.myFile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileMapper extends RowMapperStrategy<myFile> {
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
