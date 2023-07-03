package notebridge1.notebridge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@XmlRootElement
public class TeacherSchedule {
    private int id;
    private int teacherId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
    private Date date;
    private Time startTime;
    private Time endTime;

    public TeacherSchedule() {
    }

    public TeacherSchedule(int teacherId, Date date, Time startTime, Time endTime) {
        this.teacherId = teacherId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TeacherSchedule(int id, int teacherId, Date date, Time startTime, Time endTime) {
        this.id = id;
        this.teacherId = teacherId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TeacherSchedule(int teacherId) {
        this.teacherId = teacherId;
        this.date = Date.valueOf(LocalDate.ofEpochDay(
                ThreadLocalRandom.current().nextLong(
                        LocalDate.now().minusMonths(6).toEpochDay(),
                        LocalDate.now().toEpochDay()
                )
        ));
        this.startTime = Time.valueOf(LocalTime.now());
        this.endTime = Time.valueOf(LocalTime.now().plusMinutes(new Random().nextInt(10, 91)));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time duration) {
        this.endTime = duration;
    }
}
