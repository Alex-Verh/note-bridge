package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The BookingDAO class provides methods to interact with the database and perform operations related to bookings.
 */
public enum BookingDAO {
    INSTANCE;

    /**
     * Returns a list of all bookings.
     *
     * @return A list of bookings.
     */
    public List<Booking> getBookings() {
        String query = "SELECT * FROM booking";
        List<Booking> bookingList = new ArrayList<>();

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);
            while (res.next()) {
                Booking booking = extractBooking(res);
                bookingList.add(booking);
            }
            return bookingList;
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Inserts a booking along with its payment. The price is gathered from the lesson ID in the booking.
     *
     * @param booking The booking to add.
     * @return The ID of the booking, -1 on failure.
     */
    public int insertBookingAlongWithPayment(Booking booking) {
        String query = """
                WITH inserted_rows AS (
                    INSERT INTO booking
                    VALUES(DEFAULT, ?, ?, ?, false, false)
                    RETURNING id
                    )
                    INSERT INTO payment
                    VALUES(DEFAULT, (SELECT id FROM inserted_rows), (SELECT price FROM lesson WHERE id = ?), CURRENT_TIMESTAMP, true)
                    RETURNING booking_id
                                """;
        Object[] args = {
                booking.getStudentId(), booking.getLessonId(), booking.getScheduleId(), booking.getLessonId()
        };
        int bookingId;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, args);
            resultSet.next();
            bookingId = resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return -1;
        }
        return bookingId;
    }

    /**
     * Gets a booking by its ID.
     *
     * @param id The ID of the booking.
     * @return The booking.
     */
    public Booking getBookingById(int id) {
        String query = """
                SELECT *
                FROM booking
                WHERE id = ?;
                """;
        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
            if (res.next()) {
                return extractBooking(res);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
        return null;
    }

    /**
     * Extracts a Booking object from a ResultSet.
     *
     * @param res The ResultSet containing the booking data.
     * @return The extracted Booking object.
     * @throws SQLException If an SQL error occurs.
     */
    private Booking extractBooking(ResultSet res) throws SQLException {
        int bookingId = res.getInt("id");
        int studentId = res.getInt("student_id");
        int lessonId = res.getInt("lesson_id");
        int scheduleId = res.getInt("schedule_id");
        boolean isCancelled = res.getBoolean("is_canceled");
        boolean isFinished = res.getBoolean("is_finished");

        return new Booking(bookingId, studentId, lessonId, scheduleId, isCancelled, isFinished);
    }

    /**
     * Deletes a booking along with its payment.
     *
     * @param id The ID of the booking.
     * @return The amount of rows affected, -1 on failure.
     */
    public int deleteBookingById(int id) {
        String query = """
                DELETE FROM booking
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{id});
    }

    /**
     * Gets a list of bookings by student ID.
     *
     * @param studentId The ID of the student.
     * @return A list of bookings.
     */
    public List<Booking> getBookingsByStudentId(int studentId) {
        String query = """
                SELECT *
                FROM booking
                WHERE student_id =?
                """;
        return getBookingList(query, new Object[]{studentId});
    }

    /**
     * Gets a list of bookings used by other methods.
     *
     * @param query The SQL query to find bookings.
     * @param args  The arguments for the prepared statement.
     * @return A list of bookings.
     */
    private List<Booking> getBookingList(String query, Object[] args) {
        List<Booking> bookingList = new ArrayList<>();

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, args);
            while (res.next()) {
                Booking booking = extractBooking(res);
                bookingList.add(booking);
            }
            return bookingList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Gets a list of bookings by schedule ID.
     *
     * @param scheduleId The schedule ID.
     * @return A list of bookings.
     */
    public List<Booking> getBookingsByScheduleId(int scheduleId) {
        String query = """
                SELECT *
                FROM booking
                WHERE schedule_id = ?
                """;
        return getBookingList(query, new Object[]{scheduleId});
    }

    /**
     * Returns the number of lessons taken by a student.
     *
     * @param studentId The ID of the student.
     * @return The amount of lessons.
     */
    public int getBookingCount(int studentId) {
        String query = """
                SELECT COUNT(b.id)
                FROM booking b
                         JOIN teacher_schedule t ON b.schedule_id = t.id
                WHERE student_id = ? AND t.day < CURRENT_TIMESTAMP;
                                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{studentId});
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Gets the number of bookings.
     *
     * @return The number of bookings.
     */
    public int getNumberOfBookings() {
        String query = "SELECT count(*) FROM booking";
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

    /**
     * Changes the booking's status to be cancelled.
     *
     * @param bookingId The ID of the booking.
     * @return The number of rows affected.
     */
    public int setBookingCanceled(int bookingId) {
        String query = """
                UPDATE booking
                SET is_canceled = true
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{bookingId});
    }

    /**
     * Changes the booking's status to be finished.
     *
     * @param bookingId The ID of the booking.
     * @return The number of rows affected.
     */
    public int setBookingFinished(int bookingId) {
        String query = """
                UPDATE booking
                SET is_finished = true
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{bookingId});
    }

    /**
     * Gets the total number of bookings.
     *
     * @return The total number of bookings.
     */
    public int getAllBookingCount() {
        String query = """
                SELECT COUNT(*)
                FROM booking
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
