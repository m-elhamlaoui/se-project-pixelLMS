package lms.pixel.backend.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.User;
import lms.pixel.backend.utils.PasswordMapper;
import lms.pixel.backend.utils.UserRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class IUserRepository {
    private final JdbcTemplate template;

    public IUserRepository(JdbcTemplate template) {
        this.template = template;
    }

    public List<User> getAllUsers() {
        RowMapper<User> rowMapper = new UserRowMapper();
        return template.query("SELECT * FROM user_", rowMapper);
    }

    public void createUser(User user, String password){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("create_user");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("u_email", user.getEmail());
        parameters.addValue("u_name", user.getName());
        parameters.addValue("u_birthdate", user.getBirthdate());
        parameters.addValue("u_phonenumber", user.getPhonenumber());
        parameters.addValue("u_role", user.getRole());
        parameters.addValue("u_password", hashPassword(password));

        jdbcCall.execute(parameters);
    }

    public void updateUser(int userid, User user, String password){ 
        String sql = "UPDATE user_ SET "
        + "email = ?, "
        + "name = ?, "
        + "birthdate = ?, "
        + "phonenumber = ?, "
        + "role = ?, "
        + "password = ? "
        + "WHERE userid = ?;";
        
        template.update(sql, user.getEmail(), user.getName(), user.getBirthdate(), user.getPhonenumber(), user.getRole(), hashPassword(password), userid);
    }

    public List<User> getUsersInTask(int taskid) {
        RowMapper<User> rowMapper = new UserRowMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid IN (SELECT userid FROM userdoestask WHERE taskid = ?)", rowMapper, taskid);
        return users;
    }

    public List<User> getUserByCourseEngagement(int courseid) {
        RowMapper<User> rowMapper = new UserRowMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid IN (SELECT userid FROM engagesin WHERE courseid = ?)", rowMapper, courseid);
        return users;
    }

    public User getUserByEmail(String email) {
        RowMapper<User> rowMapper = new UserRowMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE email = ?", rowMapper, email);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public User getByuserId(int userid) {
        RowMapper<User> rowMapper = new UserRowMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid = ?", rowMapper, userid);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public String getPassword(int userid){
        RowMapper<String> rowMapper = new PasswordMapper();
        List<String> passwords = template.query("SELECT password FROM user_ WHERE userid = ?", rowMapper, userid);
        if (passwords.isEmpty()) {
            return null;
        }
        return passwords.get(0);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
