package lms.pixel.backend.unitTesting;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import lms.pixel.backend.controller.FileController;
import lms.pixel.backend.model.myFile;
import lms.pixel.backend.repository.FileRepository;
import lms.pixel.backend.utils.FileValidator;
import lms.pixel.backend.utils.FileStorageManager;
import lms.pixel.backend.config.PropertiesConfig;
import lms.pixel.backend.security.PermissionChecker;  // Import PermissionChecker

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {
    
    @Mock
    private FileRepository fileRepository;
    
    @Mock
    private FileValidator fileValidator;
    
    @Mock
    private FileStorageManager fileStorageManager;
    
    @Mock
    private PropertiesConfig propertiesConfig;
    
    @Mock
    private PermissionChecker permissionChecker;  // Mock PermissionChecker
    
    @InjectMocks
    private FileController fileController;
    
    @BeforeEach
    void setUp() {
        // Use lenient stubbing to avoid unnecessary stubbing errors
        lenient().when(propertiesConfig.getFileStoragePath()).thenReturn("/mock/path/to/files");
        lenient().when(permissionChecker.getUseridByToken(anyString())).thenReturn(1);
    }

    @Test
    void uploadFile_WithValidFile_ShouldUpload() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "test content".getBytes()
        );
        when(fileValidator.validate(file)).thenReturn(null);
        when(fileStorageManager.storeFile(file)).thenReturn("path/to/file");
        
        // Act
        var response = fileController.uploadFile(file, 1, "COURSE", "token");
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileRepository).attach(anyString(), anyInt(), anyInt(), anyString());
    }
    
    @Test
    void getFileEntity_WithValidId_ShouldReturnFile() {
        // Arrange
        int fileId = 1;
        myFile expectedFile = new myFile();
        when(fileRepository.getFileById(fileId)).thenReturn(expectedFile);
        
        // Act
        var response = fileController.getFileEntity(fileId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedFile, response.getBody());
    }
}
