package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This enum class provides data access methods for managing notifications in the database.
 */
public enum NotificationDAO {
    INSTANCE;

    /**
     * Retrieves a notification by its ID.
     *
     * @param id The ID of the notification.
     * @return The Notification object, or {@code null} if it does not exist.
     */
    public Notification getNotificationById(int id) {

        String query = """
                SELECT *
                FROM notification
                WHERE id = ?;
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            resultSet.next();
            return extractNotification(resultSet);
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return null;
        }
    }

    private Notification extractNotification(ResultSet resultSet) throws SQLException {
        int messageId = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        String text = resultSet.getString("text");
        Date date = resultSet.getDate("date");
        boolean isConfirmed = resultSet.getBoolean("is_confirmed");
        int senderId = resultSet.getInt("sender_id");
        int bookingId = resultSet.getInt("booking_id");

        return new Notification(messageId, userId, text, date, isConfirmed, senderId, bookingId);
    }

    /**
     * Retrieves all notifications for a user, sorted by date in descending order.
     *
     * @param id The ID of the user.
     * @return A list of notifications, or an empty list if there are none.
     */
    public List<Notification> getNotificationsForUser(int id) {
        String query = """
                SELECT *
                FROM notification
                WHERE user_id = ?
                ORDER BY date DESC;
                """;
        List<Notification> notificationList = new ArrayList<>();
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            while (resultSet.next()) {
                notificationList.add(extractNotification(resultSet));
            }
            return notificationList;
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Adds a new notification to the database.
     *
     * @param notification The notification to add.
     * @return The ID of the notification in the database, or -1 if the insert failed.
     */
    public int addNotification(Notification notification) {
        String query = """
                INSERT INTO notification
                VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{
                notification.getUserId(), notification.getText(), notification.getDate(),
                notification.isConfirmed(), notification.getSenderId(), notification.getBookingId() });
        int id;
        try {
            resultSet.next();
            id = resultSet.getInt(1);
            return id;
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return -1;
        }
    }

    /**
     * Confirms a notification by setting the "is_confirmed" flag to true.
     *
     * @param id The ID of the notification to confirm.
     * @return {@code true} if the notification was confirmed successfully, {@code false} otherwise.
     */
    public boolean confirmNotification(int id) {
        String query = """
                UPDATE notification
                SET is_confirmed = true
                WHERE id = ?
                """;
        Object[] args = { id };
        int res = Database.INSTANCE.getPreparedStatementUpdate(query, args);
        return res > 0;
    }

    /**
     * Deletes a notification from the database.
     *
     * @param id The ID of the notification to delete.
     * @return {@code true} if the notification was deleted successfully, {@code false} otherwise.
     */
    public boolean deleteNotification(int id) {
        String query = """
                DELETE FROM notification
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{id}) > 0;
    }

    /**
     * Retrieves the total count of notifications in the database.
     *
     * @return The number of notifications in the database, or -1 if an error occurred.
     */
    public int getNotificationCount() {
        String query = """
                SELECT COUNT(*)
                FROM notification
                """;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }
}
