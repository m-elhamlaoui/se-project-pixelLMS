package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

import lms.pixel.backend.model.Message;


public class MessageMapper extends RowMapperStrategy<Message> {
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message msg = new Message();
        msg.setMessageid(rs.getInt("messageid"));
        msg.setDiscussionid(rs.getInt("discussionid"));
        msg.setUserid(rs.getInt("userid"));
        msg.setContent(rs.getString("content"));
        msg.setTimestamp(rs.getTimestamp("timestamp"));
        msg.setUsername(rs.getString("name"));
        return msg;
    }
    
}
