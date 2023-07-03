package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// FIXME ONLY FOR TESTING PURPOSES!!
public enum RandomPlusCountDAO {
    INSTANCE;

    /**
     * Returns the amount of rows in the given table
     *
     * @param tableName name of the table
     * @return amount of rows
     */
    public int getRowCountOfTable(String tableName) {
        String query = "SELECT COUNT(*)\n" +
                       "FROM " + tableName;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        return extractNumber(query, resultSet);
    }

    public int getRandomId(String tablename) {
        String query = "SELECT id\n" +
                       "FROM " + tablename +
                       " ORDER BY random() \n" +
                       "LIMIT 1\n";
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        return extractNumber(query, resultSet);
    }

    private static int extractNumber(String query, ResultSet resultSet) {
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    public int getRandomInstrumentId(int randomTeacherId) {
        String query = """
                SELECT instrument_id
                FROM teacher_instruments
                WHERE teacher_id = ?
                ORDER BY random()
                """;
        return extractNumber(query, Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{randomTeacherId}));
    }

    public int getUserIdNotTeacher() {
        String query = """
                SELECT id
                FROM USERS
                WHERE id NOT IN (SELECT id FROM teacher)
                ORDER BY random()
                LIMIT 1;
                """;
        return extractNumber(query, Database.INSTANCE.getPreparedStatementQuery(query));
    }

    public int getStudentOfTeacherId(int teacherId) {
        String query = """
                    SElECT b.student_id
                    FROM booking b JOIN lesson l ON b.lesson_id = l.id
                    WHERE b.is_finished = true AND l.teacher_id = ?
                    ORDER BY random();
                """;
        return extractNumber(query, Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{teacherId}));
    }

    public List<Integer> getTeacherWithStudent() {
        String query = """
                SELECT t.id, l.id
                FROM teacher t JOIN lesson l on t.id = l.teacher_id JOIN booking b on l.id = b.lesson_id
                WHERE b.is_finished = true
                ORDER BY random()
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        try {
            resultSet.next();
            return List.of(resultSet.getInt(1), resultSet.getInt(2));
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
    }

    /**
     * Gets a lesson id from a teacher who has a schedule
     * @return
     */
    public int getLessonId() {
        String query = """
                SELECT l.id
                FROM lesson l JOIN teacher t on l.teacher_id = t.id JOIN teacher_schedule ts on t.id = ts.teacher_id
                ORDER BY random()
                """;
        return extractNumber(query, Database.INSTANCE.getPreparedStatementQuery(query));
    }

    public int getScheduleId(int randomLessonId) {
        String query = """
                SELECT ts.id
                FROM lesson l join teacher_schedule ts on l.teacher_id = ts.teacher_id
                WHERE l.id = ?
                ORDER BY random()
                """;
        return extractNumber(query, Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{randomLessonId}));

    }

    public boolean checkBookingExists(int randomLessonId, int scheduleId) {
        String query = """
                SELECT EXISTS(
                SELECT *
                FROM booking
                WHERE schedule_id = ? AND lesson_id = ?
                )
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{scheduleId, randomLessonId});
        try {
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            return false;
        }
    }

    public Booking getNewValidBooking() {
        String query = """
                SELECT u.id, l.id, ts.id, false, true
                FROM users u, teacher t join lesson l on t.id = l.teacher_id join teacher_schedule ts on t.id = ts.teacher_id
                WHERE ts.id NOT IN (SELECT schedule_id FROM booking)
                ORDER BY random()
                """;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            resultSet.next();
            return new Booking(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3), false, true);
        } catch (SQLException e) {
            return null;
        }
    }
}
