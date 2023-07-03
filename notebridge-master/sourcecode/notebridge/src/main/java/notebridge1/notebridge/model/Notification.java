package notebridge1.notebridge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;

@XmlRootElement
public class Notification {
    private int id;
    private int userId;
    private String text;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
    private Date date;
    private boolean isConfirmed;
    private int senderId;
    private int bookingId;

    public Notification() {
    }

    public Notification(int id, int userId, String text, Date date, boolean isConfirmed, int senderId, int bookingId) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.isConfirmed = isConfirmed;
        this.senderId = senderId;
        this.bookingId = bookingId;
    }
    public Notification(int userId, String text) {
        this.userId = userId;
        this.text = text;
        this.date = new Date(System.currentTimeMillis());
    }

    public Notification(int userId, String text, Date date, boolean isConfirmed, int senderId, int bookingId) {
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.isConfirmed = isConfirmed;
        this.senderId = senderId;
        this.bookingId = bookingId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
}
