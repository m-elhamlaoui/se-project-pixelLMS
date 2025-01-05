package lms.pixel.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.exceptions.NotFoundException;
import lms.pixel.backend.exceptions.OperationNotAuthorizedException;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.UserRepository;
import lms.pixel.backend.security.PermissionChecker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;



@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;

    public UserController(UserRepository userRepository, PermissionChecker permissionChecker) {
        this.userRepository = userRepository;
        this.permissionChecker = permissionChecker;
    }

    @GetMapping 
    public List<User> getAll() {
        return userRepository.getAllUsers();
    }

    @PostMapping  
    public ResponseEntity<String> create(@RequestBody User user, @RequestHeader("Authorization") String token, @RequestHeader("Password") String password) throws OperationNotAuthorizedException {
        
        int userid = permissionChecker.getUseridByToken(token);
        int roleid = User.ConvertRole(user.getRole()); 
        boolean isAuthorized = permissionChecker.checkPermission("canCreateUsers", userid, roleid);
        if (!isAuthorized) {
            throw new OperationNotAuthorizedException("You do not have permission to create this user", userid);
        }

        userRepository.createUser(user, password);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    @PutMapping("/{userid}")
    public ResponseEntity<String> update(@PathVariable int userid, @RequestBody User user, @RequestHeader("Authorization") String token, @RequestHeader("Password") String password) throws OperationNotAuthorizedException {
        
        int loggedUserid = permissionChecker.getUseridByToken(token);
        int roleid = User.ConvertRole(user.getRole());
        boolean isAuthorized = permissionChecker.checkPermission("canCreateUsers", loggedUserid, roleid);
        if (!isAuthorized) {
            throw new OperationNotAuthorizedException("You do not have permission to update this user", loggedUserid);
        }

        userRepository.updateUser(userid, user, password);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }

    @GetMapping("/{userid}")
    public User getById(@PathVariable int userid) throws NotFoundException {
        User u = userRepository.getByuserId(userid);
        if (u == null) {
            throw new NotFoundException();
        }
        return u;
    }

    @GetMapping("/current")
    public User getLoggedUser(@RequestHeader("Authorization") String token) throws NotFoundException {
        int userid = permissionChecker.getUseridByToken(token);
        if (userid == -1) {
            throw new NotFoundException();
        }
        return userRepository.getByuserId(userid);
    }

    @GetMapping("/workson/{courseid}") // Get users in a course
    public List<User> getUsersInCourse(@PathVariable int courseid) {
        List<User> users = userRepository.getUserByCourseEngagement(courseid);
        return users;
    }

    @GetMapping("/does/{taskid}") // Get users in a task
    public List<User> getUsersInTask(@PathVariable int taskid) {
        List<User> users = userRepository.getUsersInTask(taskid);
        return users;
    }

    @GetMapping("getbyemail/{useremail}")
    public User getbyemail(@PathVariable String useremail) throws NotFoundException {
        User u = userRepository.getUserByEmail(useremail);
        if (u == null) {
            throw new NotFoundException();
        }
        return u;
    }    
}
