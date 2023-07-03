package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.User;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Data Access Object (DAO) for managing User objects in the database.
 */
public enum UserDAO {
    INSTANCE;

    private byte[] pepper;

    /**
     * Constructs a new UserDAO instance and reads the pepper value from the environment variable.
     */
    UserDAO() {
        ReadPepper();
    }

    /**
     * Reads the pepper value from the environment variable.
     */
    private void ReadPepper() {
        String pepperString = System.getenv("ARGON2_PEPPER");
        pepper = Base64.getDecoder().decode(pepperString);
    }

    /**
     * Extracts a User object from a ResultSet.
     *
     * @param res the ResultSet containing the user data
     * @return a User object populated with data from the ResultSet
     * @throws SQLException if an error occurs while retrieving data from the ResultSet
     */
    private User extractUser(ResultSet res) throws SQLException {
        int userId = res.getInt("id");
        String email = res.getString("email");
        String password = res.getString("password");
        String fullName = res.getString("full_name");
        String country = res.getString("country");
        String city = res.getString("city");
        String description = res.getString("description");
        String pfpPath = res.getString("pfp_path");
        boolean online = res.getBoolean("online");

        return new User(userId, email, password, fullName, country, city, pfpPath, description, online);
    }

    /**
     * Returns the user with the given id, or null if they do not exist.
     *
     * @param id id of the user
     * @return the User object
     */
    public User getUserById(int id) {
        String query = """
                SELECT *
                FROM users
                WHERE id = ?;
                """;
        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
            if (res.next()) {
                return extractUser(res);
            }
        } catch (SQLException e) {
            System.err.println("Error from query " + query + " : " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Inserts a new user into the database.
     * Checks if the user already exists by email before inserting and performs other security validations.
     *
     * @param user The user to be inserted.
     * @return the id of the user, -1 if inserting.
     */
    public int insertUser(User user) {
        if (checkUserExistsByEmail(user.getEmail().toLowerCase())) {
            System.out.println("This email is already in use.");
            return -1;
        }
        if (!Security.validateName(user.getFullName())) {
            System.out.println("Invalid Full Name.");
            return -1;
        }
        if (!Security.validateEmail(user.getEmail().toLowerCase())) {
            System.out.println("Invalid email.");
            return -1;
        }
        if (!Security.validatePassword(user.getPassword())) {
            System.out.println("Invalid password.");
            return -1;
        }
        if (!Security.validateCityCountry(user.getCity())) {
            System.out.println("Invalid city.");
            return -1;
        }
        if (!Security.validateCityCountry(user.getCountry())) {
            System.out.println("Invalid country.");
            return -1;
        }
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // Salt length in bytes
        random.nextBytes(salt);

        String password = user.getPassword();
        byte[] hashPassword = hashPasswordArgon2(password, salt);

//        System.out.println("Password: " + password);
//        System.out.println("Hashed password as bytes: " + Arrays.toString(hashPassword));
//        System.out.println("Hashed password as string: " + new String(hashPassword));
//        System.out.println("Salt as bytes: " + Arrays.toString(salt));
//        System.out.println("Salt as string: " + new String(salt));

        String query = "INSERT INTO users(id, email, full_name, country, city, password_salt, password, description)\n" +
                "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)" +
                "RETURNING id";
        Object[] args = { user.getEmail(), user.getFullName(),
                user.getCountry(), user.getCity(), salt, hashPassword, user.getDescription() };
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, args);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Hashes a password using the Argon2 algorithm.
     * @param password the password to be hashed
     * @param salt the salt value used for hashing
     * @return the hashed password as a byte array
     */
    private byte[] hashPasswordArgon2(String password, byte[] salt) {
        // Set up Argon2 parameters
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(10)
                .withMemoryPowOfTwo(16)  // 2^16 = 65536 KB
                .withParallelism(1)
                .withSalt(salt)
                .withSecret(pepper)
                .build();

        // Generate the hash
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        byte[] hash = new byte[32];  // Argon2 generates a 32-byte hash
        generator.generateBytes(password.getBytes(), hash);
        return hash;
    }

    /**
     * Verifies a password by hashing it with the provided salt and pepper, and comparing the resulting hash
     * with the stored hash.
     * @param password The password to verify.
     * @param salt The salt used for hashing.
     * @param storedHash The stored hash to compare with.
     * @return True if the password matches the stored hash, false otherwise.
     */
    public boolean verifyPasswordArgon2(String password, byte[] salt, byte[] storedHash) {
        byte[] newHash = hashPasswordArgon2(password, salt);
        return Arrays.equals(storedHash, newHash);
    }

    /**
     * Checks if a user with the specified email already exists in the database.
     * @param email The email to be checked.
     * @return {@code true} if a user with the specified email exists, {@code false} otherwise.
     */
    public boolean checkUserExistsByEmail(String email) {
        String query = """
                SELECT CASE
                WHEN EXISTS (
                  SELECT *
                  FROM users
                  WHERE LOWER(users.email) = LOWER(?)
                ) THEN TRUE
                ELSE FALSE
                END AS result""";

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{email});
            if (res.next()) {
                System.out.println("Checking if email exists: " + email);
                System.out.println("Check result: " + res.getString(1).equalsIgnoreCase("t"));
                return res.getString(1).equalsIgnoreCase("t");
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return false;
        }
        return false;
    }

    /**
     * Deletes a user from the database based on the specified user ID.
     * @param userId the ID of the user to delete.
     * @return if the deletion was successful
     */
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        Object[] args = { userId };
        boolean deleteUserRes = Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;

        query = "DELETE FROM teacher WHERE id = ?";
        boolean deleteTeacherRes = Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;

        return deleteUserRes && deleteTeacherRes;
    }

    /**
     * Updates an existing user in the database with the provided user information.
     * @param newUser The new user object containing the updated user information.
     * @return true if the user is updated successfully, false otherwise.
     */
    public boolean updateUser(User newUser) {
        if (!checkUserExistsByEmail(newUser.getEmail())) {
            System.out.println("User not found, email: " + newUser.getEmail());
            return false;
        }

        String query = """
                UPDATE users
                SET email        = ?,
                    full_name    = ?,
                    country = ?,
                    city         = ?
                WHERE id = ?;
                """;
        Object[] args = {newUser.getEmail().toLowerCase(), newUser.getFullName(), newUser.getCountry(), newUser.getCity(), newUser.getId()};

        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }

    /**
     * Updates an existing user in the database with the provided user information.
     * @param id The ID of the user to update.
     * @param fullName The new full name of the user.
     * @param isOnline The online status of the user ("1" for online, any other value for offline).
     * @param country The new country of the user.
     * @param city The new city of the user.
     * @return true if the user is updated successfully, false otherwise.
     */
    public boolean updateUser(int id, String fullName, String isOnline, String country, String city) {
        if (getUserById(id) == null) {
            System.out.println("User not found, id: " + id);
            return false;
        }

        if (!Security.validateName(fullName)) {
            System.out.println("Invalid Full Name.");
            return false;
        }
        if (!Security.validateCityCountry(city)) {
            System.out.println("Invalid city.");
            return false;
        }
        if (!Security.validateCityCountry(country)) {
            System.out.println("Invalid country.");
            return false;
        }

        boolean onlineBoolean = isOnline.equalsIgnoreCase("1");

        System.out.println("UPDATING USER, id: " + id + ", full_name: " + fullName);

        String query = """
                UPDATE users
                SET full_name = ?,
                    country   = ?,
                    city      = ?,
                    online    = ?
                WHERE id = ?
                """;
        Object[] args = { fullName, country, city, onlineBoolean, id };
        int res = Database.INSTANCE.getPreparedStatementUpdate(query, args);

        System.out.println("UPDATE USER FOR id: " + id + " RESULT = " + res);

        return res > 0;
    }

    /**
     * Updates the description of a user in the database.
     * @param id The ID of the user to update.
     * @param description The new description of the user.
     * @return true if the user's description is updated successfully, false otherwise.
     */
    public boolean updateUserDescription(int id, String description) {
        if (getUserById(id) == null) {
            System.out.println("User not found, id: " + id);
            return false;
        }
        if (!Security.validateLessonDescription(description)) {
            System.out.println("Invalid description.");
            return false;
        }
        String query = """
                UPDATE users
                SET description = ?
                WHERE id = ?
                """;
        Object[] args = { description, id };
        int res = Database.INSTANCE.getPreparedStatementUpdate(query, args);

        System.out.println("UPDATE USER DESCRIPTION FOR id: " + id + " RESULT = " + res);

        return res > 0;
    }

    /**
     * Retrieves the ID associated with a given email from the database.
     * @param email the email for which to retrieve the ID
     * @return the ID associated with the email, or -1 if the email is not found.
     */
    public int getIdByEmail(String email) {
        String query = """
                SELECT id
                FROM users
                WHERE email = ?;
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{email});
        int id;
        try {
            resultSet.next();
            id = resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
        return id;
    }

    /**
     * Retrieves a User object based on the provided email.
     * @param email The email address of the user.
     * @return The User object corresponding to the email, or null if no user with the specified email is found.
     */
    public User getUserByEmail(String email) {
        String query = """
                SELECT *
                FROM users
                WHERE email = ?;
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{email});
        try {
            if (res.next()) {
                return extractUser(res);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
        return null;
    }

    /**
     * Returns the amount of users
     * @return amount of users or -1 if sql query failed
     */
    public int countUsers() {
        String query = """
                SELECT COUNT(*)
                FROM users
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Returns the distinct amount of cities in the database.
     * @return amount of distinct cities
     */
    public int countCities() {
        String query = """
                SELECT COUNT(DISTINCT u.city)
                FROM users u;
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Sets the online status of a user to true.
     * @param id The ID of the user.
     * @return true if the user's online status is updated successfully, false otherwise.
     */
    public boolean setOnline(int id){
        String query = """
                UPDATE users
                SET online = TRUE
                WHERE id = ?
                """;
        Object[] args = {id};
        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }

    /**
     * Sets the online status of a user to false.
     * @param id The ID of the user.
     * @return true if the user's online status is updated successfully, false otherwise.
     */
    public boolean setOffline(int id){
        String query = """
                UPDATE users
                SET online = FALSE
                WHERE id = ?
                """;
        Object[] args = {id};
        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }

    /**
     * Updates the password of a user in the database.
     * @param email The email address of the user.
     * @param password The new password for the user.
     */
    public void updatePassword(String email, String password) {
        String query = """
                UPDATE users
                SET password        = ?,
                    password_salt   = ?
                WHERE email         = ?
                """;

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // Salt length in bytes
        random.nextBytes(salt);
        byte[] hashPassword = hashPasswordArgon2(password, salt);

        Object[] args = {hashPassword, salt, email};
        Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }
}
