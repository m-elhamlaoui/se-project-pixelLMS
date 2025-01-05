package lms.pixel.backend.security.permissionCommand;

import java.util.List;

import org.springframework.stereotype.Component;

import lms.pixel.backend.model.Course;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.repository.UserRepository;

@Component
public class IsParticipant extends BasePermissionCheck {

    public IsParticipant(UserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository) {
        super(userRepository, courseRepository, taskRepository);
    }

    @Override
    public boolean execute(int performerId, int resourceId) {
        Course course = courseRepository.getByCourseId(resourceId);
        if (course.getUserid() == performerId) {
            return true;
        }
        List<User> users = userRepository.getUserByCourseEngagement(resourceId);
        for (User user : users) {
            if (user.getUserid() == performerId) {
                return true;
            }
        }

        return true;
    }
    
}
