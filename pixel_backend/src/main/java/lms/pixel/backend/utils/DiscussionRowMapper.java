package lms.pixel.backend.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import lms.pixel.backend.model.Discussion;

public class DiscussionRowMapper  implements RowMapper<Discussion>  {
    public Discussion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Discussion disc = new Discussion();
        disc.setCourseid(rs.getInt("courseid"));
        disc.setDiscussionid(rs.getInt("discussionid"));
        disc.setTitle(rs.getString("title"));
        disc.setDescription(rs.getString("description"));
        return disc;
    }
}
