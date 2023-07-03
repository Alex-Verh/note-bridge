package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Booking {

    private int id;
    private int studentId;
    private int lessonId;
    private int scheduleId;
    private boolean isCancelled;
    private boolean isFinished;

    private Booking(){

    }

    public Booking(int id, int studentId, int lessonId, int scheduleId, boolean isCancelled, boolean isFinished) {
        this.id = id;
        this.studentId = studentId;
        this.lessonId = lessonId;
        this.scheduleId = scheduleId;
        this.isCancelled = isCancelled;
        this.isFinished = isFinished;
    }
    public Booking(int studentId, int lessonId, int scheduleId, boolean isCancelled, boolean isFinished) {
        this.studentId = studentId;
        this.lessonId = lessonId;
        this.scheduleId = scheduleId;
        this.isCancelled = isCancelled;
        this.isFinished = isFinished;
    }

    public Booking(int studentId, int lessonId, int scheduleId) {
        this.studentId = studentId;
        this.lessonId = lessonId;
        this.scheduleId = scheduleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
