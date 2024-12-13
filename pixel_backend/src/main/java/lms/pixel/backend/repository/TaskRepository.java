package lms.pixel.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lms.pixel.backend.model.Task;
import lms.pixel.backend.utils.TaskRowMapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.List;
import java.util.Map;


@Repository
public class TaskRepository {

    private final JdbcTemplate template;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public void deleteTask(int taskid) {
        String sql = "DELETE FROM task WHERE taskid = ?";
        template.update(sql, taskid);
    }

    public void updateTask(int taskid, Task task) {
        String sql = "UPDATE task SET title = ?, description = ?, status = ?, duedate = ?, courseid = ? WHERE taskid = ?";
        template.update(sql, task.getTitle(), task.getDescription(), task.getStatus(), task.getDuedate(), task.getCourseid(), taskid);
    }

    public void createTask(Task task, int taskdoer) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("create_task");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("t_title", task.getTitle());
        parameters.addValue("t_description", task.getDescription());
        parameters.addValue("t_duedate", task.getDuedate());
        parameters.addValue("t_status", task.getStatus());
        parameters.addValue("t_courseid", task.getCourseid());
        parameters.addValue("taskdoer", taskdoer);

        jdbcCall.execute(parameters);
    }

    public Task getTaskById(int taskid) {
        RowMapper<Task> rowMapper = new TaskRowMapper();
        String sql = "SELECT * FROM task WHERE taskid = ?";
        List<Task> tasks = template.query(sql, rowMapper, taskid);
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.get(0);
    }  

    public List<Task> getTasksByCourse(int courseid) {
        RowMapper<Task> rowMapper = new TaskRowMapper();
        String sql = "SELECT * FROM task WHERE courseid = ?";
        return template.query(sql, rowMapper, courseid);
    }

    public List<Task> getTasksByUser(int userid) {
        RowMapper<Task> rowMapper = new TaskRowMapper();
        String sql = """
                SELECT task.* 
                FROM task
                JOIN userdoestask ON task.taskid = userdoestask.taskid
                WHERE userdoestask.userid = ?;
        """;
        return template.query(sql, rowMapper, userid);
    }

    
    public void updateAssignments(int taskid, List<List<Object>> userids) {
        if (userids.isEmpty()) {
            return;
        }
    
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(template)
            .withProcedureName("assign_to_task");
    
        for (List<Object> userid : userids) {
            Boolean isSelected = (Boolean) userid.get(0); 
            if (isSelected) {
                MapSqlParameterSource parameters = new MapSqlParameterSource();
                parameters.addValue("t_taskid", taskid);
                parameters.addValue("t_userid", userid.get(1));
                jdbcCall.execute(parameters);
            } else {
                String sql = "DELETE FROM userdoestask WHERE taskid = ? AND userid = ?";
                template.update(sql, taskid, userid.get(1));
            }
        }
    }    
}

