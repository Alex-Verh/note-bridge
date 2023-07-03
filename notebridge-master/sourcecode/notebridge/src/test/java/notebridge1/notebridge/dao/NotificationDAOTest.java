package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

class NotificationDAOTest {
    NotificationDAO dao = NotificationDAO.INSTANCE;
    @Test
    void testGetNotificationById() {
        Notification notification = dao.getNotificationById(10541);
        Assertions.assertNotNull(notification);
        Assertions.assertEquals(notification.getText(), "Me grow why. Natural spring society assume include. Card ready about read.\n" +
                "Soon ago police. House candidate source performance.");
        Assertions.assertEquals(notification.getUserId(), 3934);
        Assertions.assertEquals(notification.getDate().toString(), "2023-02-18");
        Assertions.assertEquals(notification.getId(), 10541);
    }

    @Test
    void testGetNotificationsForUser() {
        List<Notification> list = dao.getNotificationsForUser(2399);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 2);
        for (Notification notification: list) {
            // Check that every notification has the same user id
            Assertions.assertEquals(notification.getUserId(), 2399);
            // Check that notifications are orderd from unread to read, and desc by date
        }
    }

    @Test
    void testAddDeleteNotification() {
        Notification notification = new Notification(2399, "Testing, Testing, Testing", new Date(System.currentTimeMillis()), true, 4772, 6352);
        int id = dao.addNotification(notification);
        Notification notificationFromDB = dao.getNotificationById(id);

        // Check adding the notification
        Assertions.assertNotEquals(id, -1);
        Assertions.assertNotNull(notificationFromDB);
        Assertions.assertEquals(notification.getText(), notificationFromDB.getText());
        Assertions.assertEquals(notification.getUserId(), notificationFromDB.getUserId());
        Assertions.assertEquals(notification.getDate().toString(), notificationFromDB.getDate().toString());

        // Check deleting the notification
        Assertions.assertTrue(dao.deleteNotification(id));
        Assertions.assertNull(dao.getNotificationById(id));
    }
}