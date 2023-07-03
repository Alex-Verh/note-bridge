package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This enum class provides data access methods for managing messages in the database.
 */
public enum MessageDAO {
    INSTANCE;

    /**
     * Retrieves a list of all the messages either sent or received by the given user.
     *
     * @param id The ID of the user.
     * @return A list of messages, or {@code null} if there was a database failure.
     */
    public List<Message> getChatHistoryOfUser(int id) {
        String query = """
                SELECT *
                FROM message
                WHERE receiver_id = ? OR sender_id = ?;
                """;
        List<Message> messageList = new ArrayList<>();

        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id, id});
            while (resultSet.next()) {
                int messageId = resultSet.getInt(1);
                int senderId = resultSet.getInt(2);
                int receiverId = resultSet.getInt(3);
                String messageText = resultSet.getString(4);
                Timestamp timestamp = resultSet.getTimestamp(5);
                messageList.add(new Message(messageId, senderId, receiverId, messageText, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return messageList;
    }

    /**
     * Adds a new message to the database and returns its ID.
     *
     * @param message The message to send.
     * @return The ID of the message in the database, or -1 if there was a failure.
     */
    public int addNewMessage(Message message) {
        if (!Security.validateMessage(message.getMessageText())) {
            System.out.println("Error: Invalid message");
            return -1;
        }
        String query = """
                INSERT INTO message
                VALUES(DEFAULT, ?, ?, ?, ?)
                RETURNING id;
                """;

        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{message.getSenderId(), message.getReceiverId(), message.getMessageText(), message.getTimestamp()});
        int id = -1;
        try {
            resultSet.next();
            id = resultSet.getInt(1);
            return id;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return id;
        }
    }

    /**
     * Deletes a message from the database based on its ID.
     *
     * @param id The ID of the message to delete.
     * @return The number of rows affected.
     */
    public void deleteMessageById(int id){
        String query = """
                DELETE FROM message
                WHERE id = ?;
                """;

        Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{id});
    }

    /**
     * Retrieves the number of messages in the database.
     *
     * @return The number of messages.
     */
    public int getNumberOfMessages(){
        String query = "SELECT count(*) FROM message";
        int numberOfRows = -1;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            resultSet.next();
            numberOfRows = resultSet.getInt(1);


        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);

        }
        return numberOfRows;
    }
}
