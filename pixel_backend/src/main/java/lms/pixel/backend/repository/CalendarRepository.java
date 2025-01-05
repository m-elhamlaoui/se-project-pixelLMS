package lms.pixel.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.Calendar;
import lms.pixel.backend.rowMapperStrategy.CalendarMapper;
import lms.pixel.backend.rowMapperStrategy.RowMapperStrategy;

import java.util.List;

@Repository
public class CalendarRepository {
    private final JdbcTemplate template;

    public CalendarRepository(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public List<Calendar> GetEventsByUserId(int userid){
        String sql = """
                 SELECT 
                    p.title as course,
                    c.eventnumber ,
                    c.courseid ,
                    c.title ,
                    c.description,
                    c.starttime,
                    c.endtime
                FROM user_ u
                JOIN engagesin e ON u.userid = e.userid
                JOIN course p ON e.courseid = p.courseid
                JOIN calendarevent c ON p.courseid = c.courseid
                WHERE u.userid = ?;
        """;

        RowMapperStrategy<Calendar> rowMapper = new CalendarMapper();
        List<Calendar> events = template.query(sql, rowMapper, userid);
        if (events.isEmpty()) {
            return null;
        }
        return events;
    }

    public void AddEvent(Calendar event){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("plan_event");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("e_title", event.getTitle());
        parameters.addValue("e_description", event.getDescription());
        parameters.addValue("e_starttime", event.getStarttime());
        parameters.addValue("e_endtime", event.getEndtime());
        parameters.addValue("e_courseid", event.getCourseid());
        jdbcCall.execute(parameters);
    }

    public Calendar GetEventById(int eventid){
        String sql = """
                SELECT 
                    p.title as course,
                    c.eventnumber ,
                    c.courseid ,
                    c.title ,
                    c.description,
                    c.starttime,
                    c.endtime
                FROM course p
                JOIN calendarevent c ON p.courseid = c.courseid
                WHERE c.eventnumber = ?;
        """;

        RowMapperStrategy<Calendar> rowMapper = new CalendarMapper();
        Calendar event = template.queryForObject(sql, rowMapper, eventid);
        return event;
    }

    public void DeleteEvent(int eventid){
        String sql = "DELETE FROM calendarevent WHERE eventnumber = ?";
        template.update(sql, eventid);
    }

}
