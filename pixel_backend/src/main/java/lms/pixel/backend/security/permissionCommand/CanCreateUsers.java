package lms.pixel.backend.security.permissionCommand;

import org.springframework.stereotype.Component;

import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.repository.UserRepository;

@Component
public class CanCreateUsers extends BasePermissionCheck {

    public CanCreateUsers(UserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository) {
        super(userRepository, courseRepository, taskRepository);
    }

    @Override
    public boolean execute(int performerId, int resourceId) {
        String role = User.ConvertRole(resourceId);
        
        if (role.equals("Unknown")) {
            return false;
        }

        User user = userRepository.getByuserId(performerId);
        if (user.getRole().equals("teacher") && role.equals("student")) {
            return true;
        }
        else if (user.getRole().equals("admin") ) {
            return true;
        }
        return false;
    }
    
}
