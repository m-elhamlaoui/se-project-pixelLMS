package lms.pixel.backend.utils;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lms.pixel.backend.Exceptions.InvalidTokenException;
import lms.pixel.backend.Exceptions.InvalidCredentialsException;

import lms.pixel.backend.model.User;

import lms.pixel.backend.auth.LoginDTO;
import lms.pixel.backend.auth.TokenStore;

import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.repository.IUserRepository;
import lms.pixel.backend.repository.TaskRepository;

@Component
public class PermissionChecker {
    private final IUserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final TokenStore tokenStore;

    public PermissionChecker(IUserRepository userRepository, CourseRepository courseRepository, 
                             TaskRepository taskRepository, TokenStore tokenStore) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.tokenStore = tokenStore;
    }

    // General permissions

    public String ValidateCreds(LoginDTO user) throws InvalidCredentialsException {

        User userFromDb = userRepository.getUserByEmail(user.getEmail());
        if (userFromDb == null) {
            throw new InvalidCredentialsException();
        }
        String password = userRepository.getPassword(userFromDb.getUserid());
        String hashed = userRepository.hashPassword(user.getPassword());
        if (userFromDb == null || !password.equals(hashed)) {
            throw new InvalidCredentialsException();
        }

        String token = UUID.randomUUID().toString();
        tokenStore.storeToken(userFromDb.getUserid(), token);
        return token;

    }

    public int getUseridByToken(String token){
        try {
            return tokenStore.getUseridByToken(token);
        } catch (InvalidTokenException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public boolean isAuthorized(String token){
        if (token == null || getUseridByToken(token) == -1) {
            return false;
        }
        return true;
    }

    // Specific Course permissions
    public boolean canAlterTask(int userid, int taskid) {
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
