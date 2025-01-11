package lms.pixel.backend.unitTest;


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

import lms.pixel.backend.controller.CalendarController;
import lms.pixel.backend.model.Calendar;
import lms.pixel.backend.repository.CalendarRepository;
import lms.pixel.backend.security.PermissionChecker;

@ExtendWith(MockitoExtension.class)
public class CalendarControllerTest {
    
    @Mock
    private CalendarRepository calendarRepository;
    
    @Mock
    private PermissionChecker permissionChecker;
    
    @InjectMocks
    private CalendarController calendarController;
    
    @Test
    void create_WithValidPermissions_ShouldCreateEvent() throws Exception {
        // Arrange
        String token = "valid-token";
        Calendar event = new Calendar();
        event.setCourseid(1);
        
        when(permissionChecker.getUseridByToken(token)).thenReturn(1);
        when(permissionChecker.checkPermission("isParticipant", 1, 1))
            .thenReturn(true);
        
        // Act
        var response = calendarController.create(event, token);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(calendarRepository).AddEvent(event);
    }
    
    @Test
    void getEventsByUserId_ShouldReturnEvents() {
        // Arrange
        int userId = 1;
        List<Calendar> expectedEvents = Arrays.asList(new Calendar(), new Calendar());
        when(calendarRepository.GetEventsByUserId(userId)).thenReturn(expectedEvents);
        
        // Act
        List<Calendar> actualEvents = calendarController.GetEventsByUserId(userId);
        
        // Assert
        assertEquals(expectedEvents, actualEvents);
    }
}