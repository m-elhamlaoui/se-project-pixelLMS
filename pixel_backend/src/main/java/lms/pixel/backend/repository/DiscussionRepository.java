package lms.pixel.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.Discussion;
import lms.pixel.backend.model.Message;
import lms.pixel.backend.utils.DiscussionRowMapper;
import lms.pixel.backend.utils.MessageRowMapper;

import java.util.List;


@Repository 
public class DiscussionRepository {

    private final JdbcTemplate template;

    public DiscussionRepository(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public List<Discussion> getDiscussionsOfCourse(int courseid){
        RowMapper<Discussion> rowMapper = new DiscussionRowMapper();
        String sql = """
            SELECT * FROM 
                discussion
            WHERE 
                courseid = ?
        """;
        List<Discussion> discs = template.query(sql, rowMapper, courseid);
        return discs;
    }

    public void createDiscussion(Discussion disc){
        String sql = """
            INSERT INTO 
                discussion (courseid, title, description)
            VALUES 
                (?, ?, ?)
        """;
        template.update(sql, disc.getCourseid(), disc.getTitle(), disc.getDescription());
    }

    public void updateDiscussion(Discussion disc){
        String sql = """
            UPDATE 
                discussion
            SET 
                title = ?,
                description = ?
            WHERE 
                discussionid = ?
        """;
        template.update(sql, disc.getTitle(), disc.getDescription(), disc.getDiscussionid());
    }

    public void deleteDiscussion(int discussionid){
        String sql = """
            DELETE FROM 
                discussion
            WHERE 
                discussionid = ?
        """;
        template.update(sql, discussionid);
    }

    public Discussion getDiscussionById(int discussionid){
        RowMapper<Discussion> rowMapper = new DiscussionRowMapper();
        String sql = """
            SELECT * FROM 
                discussion
            WHERE 
                discussionid = ?
        """;
        Discussion disc = template.queryForObject(sql, rowMapper, discussionid);
        return disc;
    }

    public List<Message> getMessagesOfDiscussion(int discussionid){
        RowMapper<Message> rowMapper = new MessageRowMapper();
        String sql = """
            SELECT 
                messageid, discussionid, message.userid, content, timestamp, name
            FROM 
                message
            JOIN 
                user_
            ON 
                message.userid = user_.userid
            WHERE 
                discussionid = ?
        """;
        List<Message> msgs = template.query(sql, rowMapper, discussionid);
        return msgs;
    }

    public void createMessage(int discussionid, int userid, String message){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("send_message");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("m_content", message);
        parameters.addValue("m_userid", userid);
        parameters.addValue("m_discussionid", discussionid);
        
        jdbcCall.execute(parameters);
    }
    
}

