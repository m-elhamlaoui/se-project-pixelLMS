package lms.pixel.backend.security.permissionCommand;

import org.springframework.stereotype.Component;

import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.repository.UserRepository;

@Component
public class CanCreateCourse extends BasePermissionCheck {

    public CanCreateCourse(UserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository) {
        super(userRepository, courseRepository, taskRepository);
    }

    @Override
    public boolean execute(int performerId, int resourceId) {
        User user = userRepository.getByuserId(performerId);
        if (user.getRole().equals("student")) {
            return false;
        }
        return true;
    }
    
}
