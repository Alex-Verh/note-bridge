package notebridge1.notebridge.dao;

import notebridge1.notebridge.dao.MessageDAO;
import notebridge1.notebridge.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

public class MessageDAOTest {
    MessageDAO dao = MessageDAO.INSTANCE;
    @Test
    public void getChatUserHistoryTest(){
        List<Message> list = dao.getChatHistoryOfUser(1444);
        for(Message message: list) {
            Assertions.assertTrue(message.getMessageText().contains("Difficult let hundred own."));
        }

    }

    @Test
    public void addDeleteMessage(){
        int rowsBeforeInsertion = dao.getNumberOfMessages();
        Instant currentTimestamp = Instant.now();
        Timestamp sqlTimestamp = Timestamp.from(currentTimestamp);
        Message message = new Message(2532, 4886, "Hello. Its Mihai", sqlTimestamp);
        int messageAddedId = dao.addNewMessage(message);
        int rowsAfterInsertion = dao.getNumberOfMessages();
        Assertions.assertEquals(rowsBeforeInsertion + 1, rowsAfterInsertion);
        dao.deleteMessageById(messageAddedId);
        int rowsAfterDeletion = dao.getNumberOfMessages();
        Assertions.assertEquals(rowsBeforeInsertion, rowsAfterDeletion);




    }



}
