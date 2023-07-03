package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.TeacherSchedule;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The TeacherScheduleDAO class provides methods for accessing and manipulating teacher schedule data in the database.
 */
public enum TeacherScheduleDAO {

    INSTANCE;

    /**
     * Gets a list of all the teacher schedules.
     *
     * @return list of teacher schedules
     */
    public List<TeacherSchedule> getTeacherSchedules() {
        String query = """
                SELECT * FROM teacher_schedule
                """;
        List<TeacherSchedule> teacherScheduleList = new ArrayList<>();
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            while (resultSet.next()) {
                TeacherSchedule teacherSchedule = extractSchedule(resultSet);
                teacherScheduleList.add(teacherSchedule);
            }
            return teacherScheduleList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    private TeacherSchedule extractSchedule(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int teacherId = resultSet.getInt("teacher_id");
        Date day = resultSet.getDate("day");
        Time startTime = resultSet.getTime("start_time");
        Time endTime = resultSet.getTime("end_time");

        return new TeacherSchedule(id, teacherId, day, startTime, endTime);
    }

    /**
     * Inserts a timeslot into the teacher schedule.
     *
     * @param teacherSchedule the teacher schedule to add
     * @return null on fail, schedule id on success
     */
    public int insertTeacherSchedule(TeacherSchedule teacherSchedule) {
        String query = """
                INSERT INTO teacher_schedule
                VALUES(DEFAULT, ?, ?, ?, ?)
                RETURNING id;
                """;
        Object[] args = {
                teacherSchedule.getTeacherId(), teacherSchedule.getDate(),teacherSchedule.getStartTime(), teacherSchedule.getEndTime()
        };
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
     * Deletes the schedule with the given id.
     *
     * @param teacherScheduleId id of the schedule
     * @return the amount of rows affected, -1 on fail
     */
    public int deleteTeacherSchedule(int teacherScheduleId) {
        String query = """
                DELETE FROM teacher_schedule
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query,
                new Object[]{teacherScheduleId});
    }

    /**
     * Returns a list of teacher schedules associated with the given teacher id.
     *
     * @param id id of the teacher
     * @return list of teacher schedules
     */
    public List<TeacherSchedule> getTeacherSchedulesByTeacherId(int id) {
        String query = """
                SELECT *
                FROM teacher_schedule
                WHERE teacher_id = ?
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        List<TeacherSchedule> teacherScheduleList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                TeacherSchedule teacherSchedule = extractSchedule(resultSet);
                teacherScheduleList.add(teacherSchedule);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return teacherScheduleList;
    }

    /**
     * Gets a teacher schedule by its id.
     *
     * @param id id of the teacher schedule
     * @return the teacher schedule
     */
    public TeacherSchedule getTeacherScheduleById(int id) {
        String query = """
                SELECT *
                FROM teacher_schedule
                WHERE id = ?
                ORDER BY day, start_time
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            resultSet.next();
            return extractSchedule(resultSet);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
    }

    /**
     * Gets a list of free teacher schedules associated with the given teacher id.
     *
     * @param id id of the teacher
     * @return list of free teacher schedules
     */
    public List<TeacherSchedule> getFreeTeacherSchedulesByTeacherId(int id) {
        String query = """
                SELECT DISTINCT t.*
                FROM teacher_schedule t
                LEFT JOIN booking b ON t.id = b.schedule_id
                WHERE t.teacher_id = ?
                  AND (b.id IS NULL OR (
                      NOT EXISTS (
                          SELECT 1
                          FROM booking b2
                          WHERE b2.schedule_id = t.id
                              AND (b2.is_canceled = false OR b2.is_finished = true)
                      )
                  ))
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        List<TeacherSchedule> teacherScheduleList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                teacherScheduleList.add(extractSchedule(resultSet));
            }
            return teacherScheduleList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the count of teacher schedules.
     *
     * @return the count of teacher schedules
     */
    public int getTeacherScheduleCount() {
        String query = """
                SELECT COUNT(*)
                FROM teacher_schedule
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
