package lms.pixel.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.auth.TokenStore;
import lms.pixel.backend.model.Course;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.utils.PermissionChecker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("api/course")
public class CourseController {

    private final CourseRepository courseRepository;
    private final TokenStore tokenStore;
    private final PermissionChecker permissionChecker;

    public CourseController(CourseRepository courseRepository, TokenStore tokenStore, PermissionChecker permissionChecker) {
        this.courseRepository = courseRepository;
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
    }   

    @PutMapping("/assign/{id}")
    public ResponseEntity<String> assignToUsers(@PathVariable("id") int id, @RequestBody List<List<Object>> traitement, @RequestHeader("Authorization") String token) {
        int userid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canAlterCourse(userid, id)) {
            return new ResponseEntity<>("You do not have permission to alter this task", HttpStatus.FORBIDDEN);
        }

        courseRepository.updateAssignments(id, traitement);
        return new ResponseEntity<>("Task assigned", HttpStatus.OK);
    }

    @GetMapping 
    public List<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    @GetMapping("/{courseid}")
    public Course getByCourseId(@PathVariable("courseid")int Courseid) {
        return courseRepository.getByCourseId(Courseid);
    }
    

    @GetMapping("/workedby/{userid}") // get Courses where user is engaged
    public List<Course> getCourseByEngagement(@PathVariable int userid) {
        return courseRepository.getCourseByEngagement(userid);
    }

    @GetMapping("/supervisedby/{userid}") 
    public List<Course> getCourseBySupervising(@PathVariable int userid) {
        return courseRepository.getCourseBySupervising(userid);
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody Course Course, @RequestHeader("Authorization") String token){
        int userid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canCreateCourse(userid)) {
            return new ResponseEntity<>("You do not have permission to create a Course", HttpStatus.FORBIDDEN);
        }
        courseRepository.createCourse(Course, userid);
        return new ResponseEntity<>("Course created", HttpStatus.CREATED);
    }

    @PutMapping("/{courseid}")
    public ResponseEntity<String> updateCourse(@PathVariable int Courseid, @RequestBody Course Course,  @RequestHeader("Authorization") String token) {
        int userid = tokenStore.getUseridByToken(token);
        if (!permissionChecker.canAlterCourse(userid, Courseid)) {
            return new ResponseEntity<>("You do not have permission to update this Course", HttpStatus.FORBIDDEN);
        }
        courseRepository.updateCourse(Courseid, Course);
        return new ResponseEntity<>("Course updated", HttpStatus.OK);
    }
    
}
