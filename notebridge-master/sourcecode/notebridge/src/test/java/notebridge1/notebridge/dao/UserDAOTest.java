package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOTest {
    UserDAO dao = UserDAO.INSTANCE;
    @Test
    public void testGetUserById(){
        Assertions.assertEquals(dao.getUserById(5035).getFullName(), "Testing Test");
        Assertions.assertEquals(dao.getUserById(5035).getId(), 5035);
        Assertions.assertEquals(dao.getUserById(5035).getEmail(), "test1237144@gmail.com");
        Assertions.assertEquals(dao.getUserById(5035).getCity(), "Deventer");
        Assertions.assertEquals(dao.getUserById(5035).getCountry(), "Netherlands");
        Assertions.assertEquals(dao.getUserById(999999), null);
    }

    @Test
    public void testInsertUpdateDeleteUser(){
        // Create a new user object and verify that a user with such emails does not exist in the db
        User user = new User("TEST123@MAIL.COM", "ValidPassword123", "Valid Name");
        if (dao.checkUserExistsByEmail(user.getEmail())) {
            dao.deleteUser(dao.getIdByEmail(user.getEmail()));
        }
        Assertions.assertFalse(dao.checkUserExistsByEmail(user.getEmail()));
        // Insert the user in the db and check that a user with such email exists in the db
        dao.insertUser(user);
        Assertions.assertTrue(dao.checkUserExistsByEmail(user.getEmail()));
        // Check that the obj user and the user from the db have the same fields
        User userFromDb = dao.getUserByEmail(user.getEmail());
        String query = "SELECT password_salt, password FROM users WHERE lower(email) = lower(?)";
        Object[] args = { user.getEmail() };

        byte[] salt = new byte[16];
        byte[] originalHash = new byte[32];

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, args);
            res.next();
            salt = res.getBytes("password_salt");
            originalHash = res.getBytes("password");
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        Assertions.assertTrue(dao.verifyPasswordArgon2(user.getPassword(), salt, originalHash));
        Assertions.assertEquals(user.getFullName(), userFromDb.getFullName());
        Assertions.assertEquals(user.getEmail(), userFromDb.getEmail());
        // Delete the user form the db and check that a user with such emails does not exist in the db
        dao.deleteUser(userFromDb.getId());
        Assertions.assertFalse(dao.checkUserExistsByEmail(user.getEmail()));

    }
    @Test
    public void testCheckUserExistsByEmail(){
        Assertions.assertTrue(dao.checkUserExistsByEmail("tirza31@example.net"));
        Assertions.assertTrue(dao.checkUserExistsByEmail("de-gratielevi@example.com"));
        Assertions.assertTrue(dao.checkUserExistsByEmail("kian02@example.org"));
        Assertions.assertFalse(dao.checkUserExistsByEmail(""));
        Assertions.assertFalse(dao.checkUserExistsByEmail("12345"));
        Assertions.assertFalse(dao.checkUserExistsByEmail("test' OR '1'='1'"));
        Assertions.assertFalse(dao.checkUserExistsByEmail("' AND 1=1 AND 1/0;"));
        Assertions.assertFalse(dao.checkUserExistsByEmail("inexesting mail"));
    }

    @Test
    public void testGetIdByEmail(){
        Assertions.assertEquals(dao.getIdByEmail("test1237144@gmail.com"), 5035);
        Assertions.assertEquals(dao.getIdByEmail("test1230544@gmail.com"), 5046);
        Assertions.assertEquals(dao.getIdByEmail("saknfsdmlfmk@gmail.com"), 5063);
    }

    @Test
    public void testUserByEmail(){
        User user = dao.getUserByEmail("alextest@gmail.com");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getFullName(), "Alex Alex");
        Assertions.assertEquals(user.getId(), 5073);
        Assertions.assertEquals(user.getCity(), "Hoogvliet");
        Assertions.assertEquals(user.getCountry(), "Netherlands");
        Assertions.assertEquals(user.getDescription(), "Unknown");
        Assertions.assertFalse(user.isOnline());
    }

    @Test
    public void testSetOnlineOffline(){
        User user = dao.getUserByEmail("kyra33@example.com");
        Assertions.assertFalse(user.isOnline());
        Assertions.assertTrue(dao.setOnline(user.getId()));
        Assertions.assertTrue(dao.getUserByEmail("kyra33@example.com").isOnline());
        Assertions.assertTrue(dao.setOffline(user.getId()));
        Assertions.assertFalse(dao.getUserByEmail("kyra33@example.com").isOnline());
    }
}
