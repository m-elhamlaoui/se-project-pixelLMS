package lms.pixel.backend.utils;

import org.springframework.jdbc.core.RowMapper;

import lms.pixel.backend.model.Course;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CourseRowMapper  implements RowMapper<Course> {
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setCourseid(rs.getInt("courseid"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setStartdate(rs.getDate("startdate").toLocalDate());
        course.setEnddate(rs.getDate("enddate").toLocalDate());
        course.setStatus(rs.getString("status"));
        course.setUserid(rs.getInt("userid"));
        course.setSupervisor(rs.getString("supervisor"));
        return course;
    }
}
