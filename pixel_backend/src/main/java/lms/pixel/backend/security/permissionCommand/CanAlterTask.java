package lms.pixel.backend.security.permissionCommand;

import org.springframework.stereotype.Component;

import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.repository.UserRepository;

@Component
public class CanAlterTask extends BasePermissionCheck {

    public CanAlterTask(UserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository) {
        super(userRepository, courseRepository, taskRepository);
    }

    @Override
    public boolean execute(int performerId, int resourceId) {
        // Todo: Implement this method
        return true;
    }

}
