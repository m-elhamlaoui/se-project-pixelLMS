package lms.pixel.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lms.pixel.backend.auth.TokenStore;
import lms.pixel.backend.model.Task;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.utils.PermissionChecker;

import java.util.List;


@RestController
@RequestMapping("api/task")
public class TaskController {
    private final TaskRepository taskRepository;
    private final TokenStore tokenStore;
    private final PermissionChecker permissionChecker;

    public TaskController(TaskRepository taskRepository, TokenStore tokenStore, PermissionChecker permissionChecker) {
        this.taskRepository = taskRepository;
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
    }

    @GetMapping("/{taskid}")
    public ResponseEntity<Task> getTask(@PathVariable int taskid) {
        Task task = taskRepository.getTaskById(taskid);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @GetMapping("/course/{courseid}")
    public ResponseEntity<List<Task>> getTasksByCourse(@PathVariable int courseid) {
        List<Task> tasks = taskRepository.getTasksByCourse(courseid);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable int userid) {
        List<Task> tasks = taskRepository.getTasksByUser(userid);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @DeleteMapping("/{taskid}")
    public ResponseEntity<String> deleteTask(@PathVariable int taskid, @RequestHeader("Authorization") String token) {
        int userid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canAlterTask(userid, taskid)) {
            return new ResponseEntity<>("You do not have permission to delete this task", HttpStatus.FORBIDDEN);
        }
        taskRepository.deleteTask(taskid);
        return new ResponseEntity<>("Task deleted", HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{taskid}")
    public ResponseEntity<String> insertOrUpdateTask(@PathVariable int taskid, @RequestHeader("Authorization") String token, @RequestBody Task taskDetails) {
        int userid = tokenStore.getUseridByToken(token);

        if (!permissionChecker.canAlterTask(userid, taskid)) {
            return new ResponseEntity<>("You do not have permission to alter this task", HttpStatus.FORBIDDEN);
        }

        Task existingTask = taskRepository.getTaskById(taskid);
        if (existingTask == null) {
            taskRepository.createTask(taskDetails, userid);
            return new ResponseEntity<>("Task created", HttpStatus.CREATED);
        } else {
            taskRepository.updateTask(taskid, taskDetails);
            return new ResponseEntity<>("Task updated", HttpStatus.OK);
        }
    }

    @PutMapping("/assign/{id}")
    public ResponseEntity<String> assignToUsers(@PathVariable int id, @RequestBody List<List<Object>> traitement, @RequestHeader("Authorization") String token) {
        int userid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canAlterTask(userid, id)) {
            return new ResponseEntity<>("You do not have permission to alter this task", HttpStatus.FORBIDDEN);
        }

        taskRepository.updateAssignments(id, traitement);
        return new ResponseEntity<>("Task assigned", HttpStatus.OK);
    }
}
