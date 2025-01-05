package lms.pixel.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.Course;
import lms.pixel.backend.rowMapperStrategy.CourseMapper;
import lms.pixel.backend.rowMapperStrategy.RowMapperStrategy;

import java.util.List;


@Repository 
public class CourseRepository {

    private final JdbcTemplate template;

    public CourseRepository(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public List<Course> getAllCourses() {
        RowMapperStrategy<Course> rowMapper = new CourseMapper();
        String sql = """
            SELECT 
                p.courseid, 
                p.title, 
                p.description, 
                p.startdate, 
                p.enddate, 
                p.status, 
                p.userid,
                s.name AS teacher
            FROM 
                course p
            JOIN 
                user_ s ON p.userid = s.userid
        """;
        return template.query(sql, rowMapper);
    }

    public void createCourse(Course Course, int creatorUserId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
        .withProcedureName("create_course");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("p_title", Course.getTitle());
        parameters.addValue("p_description", Course.getDescription());
        parameters.addValue("p_status", Course.getStatus());
        parameters.addValue("p_creator_userid", creatorUserId);
        parameters.addValue("p_end_date", Course.getEnddate());

        jdbcCall.execute(parameters);
    }

    public void updateCourse(int Courseid, Course Course) {
        String sql = """
            UPDATE 
                course 
            SET 
                title = ?, 
                description = ?, 
                status = ?, 
                enddate = ? 
            WHERE 
                courseid = ?
        """;
        template.update(sql, Course.getTitle(), Course.getDescription(), Course.getStatus(), Course.getEnddate(), Courseid);
    }


    public Course getByCourseId(int Courseid) {
        RowMapperStrategy<Course> rowMapper = new CourseMapper();
        String sql = """
            SELECT 
                p.courseid, 
                p.title, 
                p.description, 
                p.startdate, 
                p.enddate, 
                p.status, 
                p.userid,
                s.name AS teacher
            FROM 
                course p
            JOIN 
                user_ s ON p.userid = s.userid
            WHERE 
                p.courseid = ?
        """;
        List<Course> Courses = template.query(sql, rowMapper, Courseid);
        if (Courses.isEmpty()) {
            return null;
        }
        return Courses.get(0);
    }


    public List<Course> getCourseByEngagement(int userid){
        RowMapperStrategy<Course> rowMapper = new CourseMapper();
        String sql = """
            SELECT
                p.courseid,
                p.title,
                p.description,
                p.startdate,
                p.enddate,
                p.status,
                p.userid,
                s.name AS teacher
            FROM
                course p
            JOIN
                engagesin e ON p.Courseid = e.courseid
            JOIN
                user_ s ON p.userid = s.userid
            WHERE
                e.userid = ?
        """;
        List<Course> Courses = template.query(sql, rowMapper, userid);
        return Courses;
    }

    public List<Course> getCourseBySupervising(int userid){
        RowMapperStrategy<Course> rowMapper = new CourseMapper();
        String sql = """
            SELECT 
                p.courseid, 
                p.title, 
                p.description, 
                p.startdate, 
                p.enddate, 
                p.status, 
                p.userid,
                s.name AS teacher
            FROM 
                course p
            JOIN 
                user_ s ON p.userid = s.userid
            WHERE 
                p.userid = ?
        """;
        List<Course> Courses = template.query(sql, rowMapper, userid);
        return Courses;
    }

    public void updateAssignments(int Courseid, List<List<Object>> userids) {
        if (userids.isEmpty()) {
            return;
        }
    
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("assign_to_course");
    
        for (List<Object> userid : userids) {
            Boolean isSelected = (Boolean) userid.get(0); 
            if (isSelected) {
                MapSqlParameterSource parameters = new MapSqlParameterSource();
                parameters.addValue("p_courseid", Courseid);
                parameters.addValue("p_userid", userid.get(1));
                jdbcCall.execute(parameters);
            } else {
                String sql = """
                    DELETE FROM 
                        engagesin 
                    WHERE 
                        courseid = ? AND userid = ?
                """;
                template.update(sql, Courseid, userid.get(1));
            }
        }   
    }

}
