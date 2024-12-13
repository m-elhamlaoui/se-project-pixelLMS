package lms.pixel.backend.utils;

import org.springframework.stereotype.Component;

import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.IUserRepository;
import lms.pixel.backend.repository.TaskRepository;

@Component
public class PermissionChecker {
    private final IUserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    public PermissionChecker(IUserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }


    // Specific Course permissions
    public boolean canAlterTask(int userid, int taskid){
        return true;
    }

    public boolean canAlterCourse(int userid, int courseid){
        return true;
    }

    public boolean isParticipant(int userid, int courseid){
        return true;
    }


    // Supervisor permissions
    public boolean canCreateUsers(int userid, String role){
        return true;
    }

    public boolean canCreateCourse(int userid){
        return true;
    }
    
    //SuperAdmin permissions
    public boolean canDisableUsers(int userid){
        return true;
    }

    public boolean canPromoteUsers(int userid){
        return true;
    }  
}
