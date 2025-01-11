package lms.pixel.backend.unitTesting;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import lms.pixel.backend.controller.UserController;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.UserRepository;
import lms.pixel.backend.security.PermissionChecker;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PermissionChecker permissionChecker;
    
    @InjectMocks
    private UserController userController;
    
    @Test
    void create_WithValidPermissions_ShouldCreateUser() throws Exception {
        // Arrange
        User user = new User();
        user.setRole("STUDENT");
        String token = "valid-token";
        String password = "password";
        
        when(permissionChecker.getUseridByToken(token)).thenReturn(1);
        when(permissionChecker.checkPermission("canCreateUsers", 1, 3))
            .thenReturn(true);
        
        // Act
        var response = userController.create(user, token, password);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository).createUser(user, password);
    }
    
    @Test
    void getLoggedUser_WithValidToken_ShouldReturnUser() throws Exception {
        // Arrange
        String token = "valid-token";
        User expectedUser = new User();
        
        when(permissionChecker.getUseridByToken(token)).thenReturn(1);
        when(userRepository.getByuserId(1)).thenReturn(expectedUser);
        
        // Act
        User actualUser = userController.getLoggedUser(token);
        
        // Assert
        assertEquals(expectedUser, actualUser);
    }
}