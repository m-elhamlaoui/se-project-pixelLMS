package lms.pixel.backend.unitTest;



import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import lms.pixel.backend.controller.TaskController;
import lms.pixel.backend.model.Task;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.security.PermissionChecker;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private PermissionChecker permissionChecker;
    
    @InjectMocks
    private TaskController taskController;
    
    @Test
    void getById_WithValidId_ShouldReturnTask() throws Exception {
        // Arrange
        int taskId = 1;
        Task expectedTask = new Task();
        when(taskRepository.getTaskById(taskId)).thenReturn(expectedTask);
        
        // Act
        var response = taskController.getById(taskId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTask, response.getBody());
    }
    
    @Test
    void delete_WithValidPermissions_ShouldDeleteTask() throws Exception {
        // Arrange
        int taskId = 1;
        String token = "valid-token";
        
        when(permissionChecker.getUseridByToken(token)).thenReturn(1);
        when(permissionChecker.checkPermission("canAlterTask", 1, taskId))
            .thenReturn(true);
        
        // Act
        var response = taskController.delete(taskId, token);
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskRepository).deleteTask(taskId);
    }
}
