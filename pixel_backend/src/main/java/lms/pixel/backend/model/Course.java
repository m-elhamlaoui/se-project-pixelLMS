package lms.pixel.backend.model;

import java.time.LocalDate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class Course {
    private int courseid;
    private String title;
    private String description;
    private LocalDate startdate;
    private LocalDate enddate;
    private String status;
    private int userid;
    private String teacher;

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartdate(){
        return startdate;
    }

    public void setStartdate(LocalDate startdate){
        this.startdate = startdate;
    }

    public LocalDate getEnddate(){
        return enddate;
    }

    public void setEnddate(LocalDate enddate){
        this.enddate = enddate;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public int getUserid(){
        return userid;
    }

    public void setUserid(int userid){
        this.userid = userid;
    }

    public String getTeacher(){
        return teacher;
    }

    public void setTeacher(String name){
        this.teacher = name;
    }
}
