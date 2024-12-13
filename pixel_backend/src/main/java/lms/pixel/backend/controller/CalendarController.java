package lms.pixel.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.Exceptions.OperationNotAuthorizedException;
import lms.pixel.backend.model.Calendar;
import lms.pixel.backend.repository.CalendarRepository;
import lms.pixel.backend.utils.PermissionChecker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("api/calendar")
public class CalendarController {
    private final CalendarRepository calendarRepository;
    private final PermissionChecker permissionChecker;

    public CalendarController(CalendarRepository calendarRepository, PermissionChecker permissionChecker) {
        this.calendarRepository = calendarRepository;
        this.permissionChecker = permissionChecker;
    }

    @GetMapping("/events/{userid}")
    public List<Calendar> GetEventsByUserId(@PathVariable int userid) {
        List<Calendar> events = calendarRepository.GetEventsByUserId(userid);
        return events;
    }

    @PostMapping 
    public ResponseEntity<String> AddEvent (@RequestBody Calendar event, @RequestHeader("Authorization") String token) throws OperationNotAuthorizedException {
        int userid = permissionChecker.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, event.getCourseid())){
            throw new OperationNotAuthorizedException("You do not have permission to create an event in this course", userid);
        }

        calendarRepository.AddEvent(event);
        return new ResponseEntity<>("Event created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventid}")
    public ResponseEntity<String> DeleteEvent(@PathVariable int eventid, @RequestHeader("Authorization") String token) throws OperationNotAuthorizedException {
        int userid = permissionChecker.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, calendarRepository.GetEventById(eventid).getCourseid())){
            throw new OperationNotAuthorizedException("You do not have permission to delete this event", userid);
        }

        calendarRepository.DeleteEvent(eventid);
        return new ResponseEntity<>("Event deleted", HttpStatus.OK);
    }
}
