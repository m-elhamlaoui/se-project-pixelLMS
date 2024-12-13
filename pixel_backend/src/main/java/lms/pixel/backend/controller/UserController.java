package lms.pixel.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.auth.TokenStore;
import lms.pixel.backend.model.User;
import lms.pixel.backend.repository.IUserRepository;
import lms.pixel.backend.utils.PermissionChecker;

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

    private final IUserRepository userRepository;
    private final TokenStore tokenStore;
    private final PermissionChecker permissionChecker;

    public UserController(IUserRepository userRepository, TokenStore tokenStore, PermissionChecker permissionChecker) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
    }

    @GetMapping 
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @PostMapping  
    public ResponseEntity<String> createUser(@RequestBody User user, @RequestHeader("Authorization") String token, @RequestHeader("Password") String password) {
        int userid = tokenStore.getUseridByToken(token);
        
        if (!permissionChecker.canCreateUsers(userid, user.getRole())) {
            return new ResponseEntity<>("You do not have permission to create a user", HttpStatus.FORBIDDEN);
        }

        userRepository.createUser(user, password);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    @PutMapping("/{userid}")
    public ResponseEntity<String> updateUser(@PathVariable int userid, @RequestBody User user, @RequestHeader("Authorization") String token, @RequestHeader("Password") String password) {
        int loggedUserid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canCreateUsers(loggedUserid, user.getRole())) {
            return new ResponseEntity<>("You do not have permission to update this user", HttpStatus.FORBIDDEN);
        }

        userRepository.updateUser(userid, user, password);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }

    @GetMapping("getbyemail/{useremail}")
    public User getbyemail(@PathVariable String useremail) {
        return userRepository.getUserByEmail(useremail);
    }

    @GetMapping("/{userid}")
    public User getUser(@PathVariable int userid) {
        return userRepository.getByuserId(userid);
    }

    @GetMapping("/current")
    public User getLoggedUser(@RequestHeader("Authorization") String token) {
        int userid = tokenStore.getUseridByToken(token);
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
    
}
