package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

import lms.pixel.backend.model.User;

public class UserMapper extends RowMapperStrategy<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserid(rs.getInt("userid"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setBirthdate(rs.getDate("birthdate").toLocalDate());
        user.setPhonenumber(rs.getString("phonenumber"));
        user.setCreationdate(rs.getDate("creationdate").toLocalDate());
        user.setRole(rs.getString("role"));
        user.setIsdeleted(rs.getBoolean("isdeleted"));
        return user;
    }

}