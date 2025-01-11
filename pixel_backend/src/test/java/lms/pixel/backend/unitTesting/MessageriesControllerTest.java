package lms.pixel.backend.unitTesting;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import lms.pixel.backend.controller.MessageriesController;
import lms.pixel.backend.model.Discussion;
import lms.pixel.backend.model.Message;
import lms.pixel.backend.repository.DiscussionRepository;
import lms.pixel.backend.security.PermissionChecker;

@ExtendWith(MockitoExtension.class)
public class MessageriesControllerTest {
    
    @Mock
    private DiscussionRepository discRepository;
    
    @Mock
    private PermissionChecker permissionChecker;
    
    @InjectMocks
    private MessageriesController messageriesController;
    
    @Test
    void createMessage_WithValidPermissions_ShouldCreateMessage() throws Exception {
        // Arrange
        int discussionId = 1;
        Message message = new Message();
        message.setContent("This is a test message."); 
        String token = "valid-token";
        Discussion discussion = new Discussion();
        discussion.setCourseid(1);
        
        when(permissionChecker.getUseridByToken(token)).thenReturn(1);
        when(discRepository.getDiscussionById(discussionId)).thenReturn(discussion);
        when(permissionChecker.checkPermission("isParticipant", 1, 1))
            .thenReturn(true);
        
        // Act
        var response = messageriesController.createMessage(discussionId, message, token);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // Verify that createMessage was called with the expected arguments
        verify(discRepository).createMessage(eq(discussionId), eq(1), eq("This is a test message."));
    }

    @Test
    void getMessagesOfDiscussion_ShouldReturnMessages() {
        // Arrange
        int discussionId = 1;
        List<Message> expectedMessages = Arrays.asList(new Message(), new Message());
        when(discRepository.getMessagesOfDiscussion(discussionId))
            .thenReturn(expectedMessages);
        
        // Act
        List<Message> actualMessages = messageriesController
            .getMessagesOfDiscussion(discussionId);
        
        // Assert
        assertEquals(expectedMessages, actualMessages);
    }
}
