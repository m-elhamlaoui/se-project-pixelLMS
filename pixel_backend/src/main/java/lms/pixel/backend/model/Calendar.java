package lms.pixel.backend.model;

import java.sql.Timestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Calendar {
    private int eventnumber;
    private int courseid;
    private String title;
    private String description;
    private Timestamp starttime;
    private Timestamp endtime;
    private String course;


    public int getEventnumber() {
        return eventnumber;
    }

    public void setEventnumber(int eventnumber) {
        this.eventnumber = eventnumber;
    }

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

    public Timestamp getStarttime() {
        return starttime;
    }

    public void setStarttime(Timestamp starttime) {
        this.starttime = starttime;
    }

    public Timestamp getEndtime() {
        return endtime;
    }

    public void setEndtime(Timestamp endtime) {
        this.endtime = endtime;
    }

    public String getCourseTitle(){
        return course;
    }

    public void setCourseTitle(String title){
        this.course = title;
    }
}
