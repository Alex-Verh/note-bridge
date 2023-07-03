package notebridge1.notebridge.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import notebridge1.notebridge.Security;

import java.sql.Date;
import java.sql.Timestamp;

@XmlRootElement
public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String messageText;
    private Timestamp timestamp;

    public Message(int senderId, int receiverId, String messageText, Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Message(int id, int senderId, int receiverId, String messageText, Timestamp timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return Security.removeTags(messageText);
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Message() {
    }
}
