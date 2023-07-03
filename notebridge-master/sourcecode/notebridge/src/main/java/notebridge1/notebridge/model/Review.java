package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;

@XmlRootElement
public class Review {
    private int id;
    private int teacherId;
    private int studentId;
    private double rating;
    private String comment;
    private int lessonId;

    private String studentName;
    private String studentCity;

    public Review() {
    }

    public Review(int teacherId, int studentId, double rating, String comment, int lessonId) {
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.lessonId = lessonId;
    }

    public Review(int teacherId, int studentId, double rating, String comment, int lessonId, String studentName, String studentCity) {
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.lessonId = lessonId;
        this.studentName = studentName;
        this.studentCity = studentCity;
    }

    public Review(int id, int teacherId, int studentId, double rating, String comment, int lessonId, String studentName, String studentCity) {
        this.id = id;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.lessonId = lessonId;
        this.studentName = studentName;
        this.studentCity = studentCity;
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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCity() {
        return studentCity;
    }

    public void setStudentCity(String studentCity) {
        this.studentCity = studentCity;
    }
}
