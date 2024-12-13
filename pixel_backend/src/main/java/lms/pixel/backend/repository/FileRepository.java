package lms.pixel.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.myFile;
import lms.pixel.backend.utils.FileRowMapper;

import java.util.List;

@Repository
public class FileRepository {
    
    private final JdbcTemplate template;

    public FileRepository(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public void attach(String path, int userId, int attachToId, String foreignKeyType) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("attach_file");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("f_path", path);
        parameters.addValue("f_userid", userId);
        parameters.addValue("attachtoID", attachToId);
        parameters.addValue("foreignKeyType", foreignKeyType);
        

        jdbcCall.execute(parameters);
    }

    public myFile getFileById(int fileId) {
        String sql = "SELECT * FROM file_ WHERE fileid = ?";
        List<myFile> files = template.query(sql, new FileRowMapper(), fileId);
        if (files.isEmpty()) {
            return null;
        }
        return files.get(0);
    }

    public List<myFile> getFilesByCourse(int courseId) {
        String sql = "SELECT * FROM file_ WHERE courseid = ?";
        return template.query(sql, new FileRowMapper(), courseId);
    }

    public List<myFile> getFilesByTask(int taskId) {
        String sql = "SELECT * FROM file_ WHERE taskid = ?";
        return template.query(sql, new FileRowMapper(), taskId);
    }

    public List<myFile> getFilesByMessage(int messageId) {
        String sql = "SELECT * FROM file_ WHERE messageid = ?";
        return template.query(sql, new FileRowMapper(), messageId);
    }

    public List<myFile> getFilesByProfile(int profileId) {
        String sql = "SELECT * FROM file_ WHERE profileid = ?";
        return template.query(sql, new FileRowMapper(), profileId);
    }

    public List<myFile> getFilesByUser(int userId) {
        String sql = "SELECT * FROM file_ WHERE userid = ?";
        return template.query(sql, new FileRowMapper(), userId);
    } 


}

