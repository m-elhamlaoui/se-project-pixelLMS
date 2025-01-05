package lms.pixel.backend.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.User;
import lms.pixel.backend.rowMapperStrategy.PasswordMapper;
import lms.pixel.backend.rowMapperStrategy.RowMapperStrategy;
import lms.pixel.backend.rowMapperStrategy.UserMapper;
import lms.pixel.backend.security.hasherFactory.HashFactory;
import lms.pixel.backend.security.hasherFactory.IHasher;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class UserRepository {
    private final JdbcTemplate template;

    public UserRepository(JdbcTemplate template) {
        this.template = template;
    }

    public List<User> getAllUsers() {
        RowMapperStrategy<User> rowMapper = new UserMapper();
        return template.query("SELECT * FROM user_", rowMapper);
    }

    public void createUser(User user, String password){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("create_user");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        IHasher hasher = HashFactory.getHasher();
        String hashPassword = hasher.hash(password);

        parameters.addValue("u_email", user.getEmail());
        parameters.addValue("u_name", user.getName());
        parameters.addValue("u_birthdate", user.getBirthdate());
        parameters.addValue("u_phonenumber", user.getPhonenumber());
        parameters.addValue("u_role", user.getRole());
        parameters.addValue("u_password", hashPassword);

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
        
        IHasher hasher = HashFactory.getHasher();
        String hashPassword = hasher.hash(password);

        template.update(sql, user.getEmail(), user.getName(), user.getBirthdate(), user.getPhonenumber(), user.getRole(), hashPassword, userid);
    }

    public List<User> getUsersInTask(int taskid) {
        RowMapperStrategy<User> rowMapper = new UserMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid IN (SELECT userid FROM userdoestask WHERE taskid = ?)", rowMapper, taskid);
        return users;
    }

    public List<User> getUserByCourseEngagement(int courseid) {
        RowMapperStrategy<User> rowMapper = new UserMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid IN (SELECT userid FROM engagesin WHERE courseid = ?)", rowMapper, courseid);
        return users;
    }

    public User getUserByEmail(String email) {
        RowMapperStrategy<User> rowMapper = new UserMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE email = ?", rowMapper, email);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public User getByuserId(int userid) {
        RowMapperStrategy<User> rowMapper = new UserMapper();
        List<User> users = template.query("SELECT * FROM user_ WHERE userid = ?", rowMapper, userid);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public String getPassword(int userid){
        RowMapperStrategy<String> rowMapper = new PasswordMapper();
        List<String> passwords = template.query("SELECT password FROM user_ WHERE userid = ?", rowMapper, userid);
        if (passwords.isEmpty()) {
            return null;
        }
        return passwords.get(0);
    }
}
