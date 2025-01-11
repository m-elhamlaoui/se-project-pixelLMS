package lms.pixel.backend.unitTesting;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lms.pixel.backend.model.Course;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.security.PermissionChecker;
import lms.pixel.backend.controller.CourseController;
import lms.pixel.backend.exceptions.NotFoundException;
import lms.pixel.backend.exceptions.OperationNotAuthorizedException;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses() {
        // Arrange
        Course course1 = new Course();
        course1.setCourseid(1);
        course1.setTitle("Java Programming");

        Course course2 = new Course();
        course2.setCourseid(2);
        course2.setTitle("Python Programming");

        List<Course> expectedCourses = Arrays.asList(course1, course2);
        when(courseRepository.getAllCourses()).thenReturn(expectedCourses);

        // Act
        List<Course> actualCourses = courseController.getAll();

        // Assert
        assertEquals(expectedCourses, actualCourses);
        verify(courseRepository).getAllCourses();
    }

    @Test
    void getById_WithValidId_ShouldReturnCourse() throws NotFoundException {
        // Arrange
        int courseId = 1;
        Course expectedCourse = new Course();
        expectedCourse.setCourseid(courseId);
        expectedCourse.setTitle("Java Programming");

        when(courseRepository.getByCourseId(courseId)).thenReturn(expectedCourse);

        // Act
        Course actualCourse = courseController.getById(courseId);

        // Assert
        assertEquals(expectedCourse, actualCourse);
        verify(courseRepository).getByCourseId(courseId);
    }

    @Test
    void getById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        int courseId = 999;
        when(courseRepository.getByCourseId(courseId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> courseController.getById(courseId));
        verify(courseRepository).getByCourseId(courseId);
    }

    @Test
    void createCourse_WithValidPermissions_ShouldCreateCourse() throws OperationNotAuthorizedException {
        // Arrange
        String token = "valid-token";
        int userId = 1;
        Course course = new Course();
        course.setTitle("New Course");

        when(permissionChecker.getUseridByToken(token)).thenReturn(userId);
        when(permissionChecker.checkPermission("canCreateCourse", userId, 0)).thenReturn(true);

        // Act
        ResponseEntity<String> response = courseController.create(course, token);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Course created", response.getBody());
        verify(courseRepository).createCourse(course, userId);
    }

    @Test
    void createCourse_WithoutPermissions_ShouldThrowException() {
        // Arrange
        String token = "valid-token";
        int userId = 1;
        Course course = new Course();
        course.setTitle("New Course");

        when(permissionChecker.getUseridByToken(token)).thenReturn(userId);
        when(permissionChecker.checkPermission("canCreateCourse", userId, 0)).thenReturn(false);

        // Act & Assert
        assertThrows(OperationNotAuthorizedException.class, () -> courseController.create(course, token));
        verify(courseRepository, never()).createCourse(any(), anyInt());
    }
}
