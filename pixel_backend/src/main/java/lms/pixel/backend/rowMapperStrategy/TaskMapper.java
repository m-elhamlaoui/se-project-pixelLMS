package lms.pixel.backend.rowMapperStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

import lms.pixel.backend.model.Task;

public class TaskMapper extends RowMapperStrategy<Task> {
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setTaskid(rs.getInt("taskid"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(rs.getString("status"));
        task.setCourseid(rs.getInt("courseid"));
        task.setDuedate(rs.getDate("duedate") != null ? rs.getDate("duedate") : null);
        return task;
    }
}
