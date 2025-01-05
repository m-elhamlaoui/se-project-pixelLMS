package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

import lms.pixel.backend.model.Course;

public class CourseMapper extends RowMapperStrategy<Course> {
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setCourseid(rs.getInt("courseid"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));

        Date startDate = rs.getDate("startdate");
        course.setStartdate(startDate != null ? startDate.toLocalDate() : LocalDate.of(2000, 1, 1));  // Arbitrary date

        Date endDate = rs.getDate("enddate");
        course.setEnddate(endDate != null ? endDate.toLocalDate() : LocalDate.of(2000, 1, 1));  // Arbitrary date

        course.setStatus(rs.getString("status"));
        course.setUserid(rs.getInt("userid"));
        course.setTeacher(rs.getString("teacher"));
        return course;
    }
}
