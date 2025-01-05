package lms.pixel.backend.security.permissionCommand;


import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.TaskRepository;
import lms.pixel.backend.repository.UserRepository;

public abstract class BasePermissionCheck implements ICommand {

    protected final UserRepository userRepository;
    protected final CourseRepository courseRepository;
    protected final TaskRepository taskRepository;

    public BasePermissionCheck(UserRepository userRepository, CourseRepository courseRepository, 
                             TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public abstract boolean execute(int performerId, int resourceId);

}
