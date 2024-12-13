package lms.pixel.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.pixel.backend.auth.TokenStore;
import lms.pixel.backend.model.Discussion;
import lms.pixel.backend.model.Message;
import lms.pixel.backend.repository.DiscussionRepository;
import lms.pixel.backend.utils.PermissionChecker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("api/discussion")
public class MessageriesController {

    private final DiscussionRepository discRepository;
    private final TokenStore tokenStore;
    private final PermissionChecker permissionChecker;

    public MessageriesController(DiscussionRepository discRepository, TokenStore tokenStore, PermissionChecker permissionChecker) {
        this.discRepository = discRepository;
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
    }   

    @PostMapping("/message/{discussionid}")
    public ResponseEntity<String> createMessage(@PathVariable int discussionid, @RequestBody Message message, @RequestHeader("Authorization") String token){
        int userid = tokenStore.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, discRepository.getDiscussionById(discussionid).getCourseid())){
            return new ResponseEntity<>("You do not have permission to create a message in this discussion", HttpStatus.FORBIDDEN);
        }

        discRepository.createMessage(discussionid, userid, message.getContent());
        return new ResponseEntity<>("Message created", HttpStatus.CREATED);
    }

    @GetMapping("/message/{discussionid}") 
    public List<Message> getMessagesOfDiscussion(@PathVariable int discussionid) {
        return discRepository.getMessagesOfDiscussion(discussionid);
    }

    @GetMapping("/{discussionid}")
    public Discussion getDiscussionById(@PathVariable int discussionid) {
        return discRepository.getDiscussionById(discussionid);
    }

    @GetMapping("/getbycourse/{courseid}") 
    public List<Discussion> getDiscussionsOfCourse(@PathVariable int courseid) {
        return discRepository.getDiscussionsOfCourse(courseid);
    }

    @PostMapping
    public ResponseEntity<String> createDiscussion(@RequestBody Discussion disc, @RequestHeader("Authorization") String token){
        int userid = tokenStore.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, disc.getCourseid())){
            return new ResponseEntity<>("You do not have permission to create a discussion in this course", HttpStatus.FORBIDDEN);
        }

        discRepository.createDiscussion(disc);
        return new ResponseEntity<>("Discussion created", HttpStatus.CREATED);
    }

    @PutMapping("/{discussionid}")
    public ResponseEntity<String> updateDiscussion(@PathVariable int discussionid, @RequestBody Discussion disc, @RequestHeader("Authorization") String token){
        int userid = tokenStore.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, disc.getCourseid())){
            return new ResponseEntity<>("You do not have permission to update this discussion", HttpStatus.FORBIDDEN);
        }

        discRepository.updateDiscussion(disc);
        return new ResponseEntity<>("Discussion updated", HttpStatus.OK);
    }

    @DeleteMapping("/{discussionid}")
    public ResponseEntity<String> deleteDiscussion(@PathVariable int discussionid, @RequestHeader("Authorization") String token){
        int userid = tokenStore.getUseridByToken(token);

        if(!permissionChecker.isParticipant(userid, discRepository.getDiscussionById(discussionid).getCourseid())){
            return new ResponseEntity<>("You do not have permission to delete this discussion", HttpStatus.FORBIDDEN);
        }

        discRepository.deleteDiscussion(discussionid);
        return new ResponseEntity<>("Discussion deleted", HttpStatus.OK);
    }
    
}
