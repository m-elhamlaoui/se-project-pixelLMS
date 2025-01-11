package lms.pixel.backend.unitTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import lms.pixel.backend.controller.AuthController;
import lms.pixel.backend.exceptions.InvalidCredentialsException;
import lms.pixel.backend.exceptions.InvalidTokenException;
import lms.pixel.backend.model.LoginDTO;
import lms.pixel.backend.utils.AuthHandler;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    
    @Mock
    private AuthHandler authService;
    
    @InjectMocks
    private AuthController authController;
    
    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password");
        String expectedToken = "valid-token";
        
        when(authService.ValidateCreds(credentials)).thenReturn(expectedToken);
        
        // Act
        var response = authController.login(null, credentials);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
    }
    
    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        when(authService.ValidateCreds(credentials))
            .thenThrow(new InvalidCredentialsException());
        
        // Act
        var response = authController.login(null, credentials);
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void logout_WithValidToken_ShouldReturnOk() throws Exception {
        // Arrange
        String token = "valid-token";
        doNothing().when(authService).invalidateToken(token);
        
        // Act
        var response = authController.logout(token);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out", response.getBody());
    }
}






