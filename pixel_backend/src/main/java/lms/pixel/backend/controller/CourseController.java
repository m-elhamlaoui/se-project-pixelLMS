package lms.pixel.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.exceptions.NotFoundException;
import lms.pixel.backend.exceptions.OperationNotAuthorizedException;
import lms.pixel.backend.model.Course;
import lms.pixel.backend.repository.CourseRepository;
import lms.pixel.backend.security.PermissionChecker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("api/course")
public class CourseController {

    private final CourseRepository courseRepository;
    private final PermissionChecker permissionChecker;

    public CourseController(CourseRepository courseRepository, PermissionChecker permissionChecker) {
        this.courseRepository = courseRepository;
        this.permissionChecker = permissionChecker;
    }   

    @GetMapping 
    public List<Course> getAll() {
        return courseRepository.getAllCourses();
    }

    @GetMapping("/{courseid}")
    public Course getById(@PathVariable("courseid")int Courseid) throws NotFoundException {
        Course course = courseRepository.getByCourseId(Courseid);
        if (course == null) {
            throw new NotFoundException();
        }
        return course;
    }
    
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Course Course, @RequestHeader("Authorization") String token) throws OperationNotAuthorizedException {
        
        int userid = permissionChecker.getUseridByToken(token);
        boolean isAuthorized = permissionChecker.checkPermission("canCreateCourse", userid, 0);
        if (!isAuthorized) {
            throw new OperationNotAuthorizedException("You do not have permission to create a Course", userid);
        }

        courseRepository.createCourse(Course, userid);
        return new ResponseEntity<>("Course created", HttpStatus.CREATED);
    }

    @PutMapping("/{courseid}")
    public ResponseEntity<String> update(@PathVariable int Courseid, @RequestBody Course Course,  @RequestHeader("Authorization") String token) throws OperationNotAuthorizedException {
        
        int userid = permissionChecker.getUseridByToken(token);
        boolean isAuthorized = permissionChecker.checkPermission("canAlterCourse", userid, Courseid);
        if (!isAuthorized) {
            throw new OperationNotAuthorizedException("You do not have permission to alter this Course", userid);
        }
        courseRepository.updateCourse(Courseid, Course);
        return new ResponseEntity<>("Course updated", HttpStatus.OK);

    }
    

    @PutMapping("/assign/{id}")
    public ResponseEntity<String> assignToUsers(@PathVariable("id") int id, @RequestBody List<List<Object>> traitement, @RequestHeader("Authorization") String token) throws OperationNotAuthorizedException {
        
        int userid = permissionChecker.getUseridByToken(token);
        boolean isAuthorized = permissionChecker.checkPermission("canAlterCourse", userid, id);
        if (!isAuthorized) {
            throw new OperationNotAuthorizedException("You do not have permission to alter this task", userid);
        }

        courseRepository.updateAssignments(id, traitement);
        return new ResponseEntity<>("Task assigned", HttpStatus.OK);
    }

    @GetMapping("/workedby/{userid}") // get Courses where user is engaged
    public List<Course> getCourseByEngagement(@PathVariable int userid) {
        return courseRepository.getCourseByEngagement(userid);
    }

    @GetMapping("/supervisedby/{userid}") 
    public List<Course> getCourseBySupervising(@PathVariable int userid) {
        return courseRepository.getCourseBySupervising(userid);
    }
    
}
