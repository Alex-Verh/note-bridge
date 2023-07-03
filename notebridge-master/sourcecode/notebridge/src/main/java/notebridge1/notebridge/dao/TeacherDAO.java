package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.NominatimAPI;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.Instrument;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.ZipcodeCoordinate;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The DAO (Data Access Object) class for managing teachers in the database.
 * This class provides methods for retrieving, inserting, updating, and deleting teacher records.
 */
public enum TeacherDAO {
    INSTANCE;

    /**
     * Retrieves the details of all teachers from the database.
     * Each teacher's information includes their teacher ID, description, experience, and average rating.
     *
     * @return the list of all teachers
     */
    public List<Teacher> getTeachers() {
        String query = "SELECT * FROM teacher";
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);
        return getTeacherList(res, query);
    }

    private List<Teacher> getTeacherList(ResultSet res, String query) {
        List<Teacher> teacherList = new ArrayList<>();
        try {
            while (res.next()) {
                Teacher teacher = extractTeacher(res);
                teacherList.add(teacher);
            }
            return teacherList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    private Teacher extractTeacher(ResultSet res) throws SQLException {
        int teacherId = res.getInt("id");
        String experience = res.getString("experience");
        double avgRating = res.getDouble("avg_rating");
        String zipcode = res.getString("zipcode");
        String videoPath = res.getString("video_path");

        return new Teacher(teacherId, experience, avgRating, zipcode, videoPath);
    }

    /**
     * Gets a teacher by their ID.
     *
     * @param id the ID of the teacher
     * @return the teacher with the specified ID, or null if not found
     */
    public Teacher getTeacherById(int id) {
        String query = """
                SELECT *
                FROM teacher
                WHERE id = ?
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            if (res.next()) {
                return extractTeacher(res);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
        return null;
    }

    /**
     * Returns the total number of teachers in the database.
     *
     * @return the number of teachers, or -1 if the database query fails
     */
    public int getTeacherCount() {
        String query = """
                SELECT COUNT(*)
                FROM teacher
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
     * Inserts a new teacher into the database.
     * Checks if the teacher already exists by ID before inserting.
     *
     * @param teacher the teacher to be inserted
     * @return the ID of the new teacher, or -1 if the insertion fails
     */
    public int insertTeacher(Teacher teacher) {
        if (checkTeacherExistsByID(teacher.getId())) {
            System.out.println("This user (ID: " + teacher.getId() + ") is already a teacher.");
            return -1;
        }

        System.out.println("received parameters: " + teacher);
        if (!Security.validateZip(teacher.getZipcode())) {
            System.out.println("Invalid zipcode.");
            return -1;
        }
        String query = """
                INSERT INTO teacher (id, experience, avg_rating, zipcode, video_path)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;
        Object[] args = { teacher.getId(), teacher.getExperience(), teacher.getAvgRating(),
                teacher.getZipcode(), teacher.getVideoPath() };

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
     * Checks if a teacher with the specified ID already exists in the database.
     *
     * @param teacherId The teacher ID to be checked.
     * @return {@code true} if a teacher with the specified ID exists, {@code false} otherwise.
     */
    public boolean checkTeacherExistsByID(int teacherId) {
        String query = """
                SELECT CASE
                WHEN EXISTS (
                  SELECT *
                  FROM teacher
                  WHERE teacher.id = ?
                ) THEN TRUE
                ELSE FALSE
                END AS result""";

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{teacherId});
            if (res.next()) {
                System.out.println("Checking if teacher ID exists: " + teacherId);
                System.out.println("Check result: " + res.getString(1).equalsIgnoreCase("t"));
                return res.getString(1).equalsIgnoreCase("t");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return false;
    }

    /**
     * Deletes a teacher from the database based on the specified teacher ID.
     * Updates the hashmap of teachers after deletion.
     *
     * @param teacherId the ID of the teacher to delete.
     */
    public int deleteTeacher(int teacherId) {
        String query = "DELETE FROM teacher WHERE id = ?";
        Object[] args = {teacherId};

        return Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }

    /**
     * Updates the details of a teacher in the database.
     *
     * @param id the ID of the teacher to update
     * @param experience the updated experience of the teacher
     * @param zipcode the updated zipcode of the teacher
     * @return {@code true} if the update is successful, {@code false} otherwise
     */
    public boolean updateTeacherDetails(int id, String experience, String zipcode) {
        zipcode = zipcode.toUpperCase().replaceAll("\\s", "");

        if (!checkTeacherExistsByID(id)) {
            System.out.println("Teacher not found, id: " + id);
            return false;
        }

        if (!processZipcode(zipcode)) {
            zipcode = "UNKNOWN";
        }
        if (!Security.validateZip(zipcode)) {
            System.out.println("Invalid zipcode.");
            return false;
        }

        String query = """
                UPDATE teacher
                SET experience = ?,
                    zipcode    = ?
                WHERE id = ?;
                """;
        Object[] args = { experience, zipcode, id };
        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }

    /**
     * Updates the list of instruments for a teacher in the database.
     *
     * @param teacherId the ID of the teacher
     * @param selectedInstruments the list of instrument IDs to be associated with the teacher
     * @return a list of booleans indicating the success or failure of each instrument update
     */
    public List<Boolean> updateTeacherInstruments(int teacherId, List<String> selectedInstruments) {
        List<Boolean> response = new ArrayList<>();

        List<Instrument> currentInstruments = TeacherInstrumentsDAO.INSTANCE.getInstrumentsByTeacherId(teacherId);

        // remove unchecked instruments
        for (Instrument instrument : currentInstruments) {

            boolean keepInstrument = false;
            for (String instrumentId : selectedInstruments) {
                try {
                    int id = Integer.parseInt(instrumentId);
                    if (instrument.getId() == id) {
                        keepInstrument = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Instrument id not integer");
                }
            }
            if (!keepInstrument) {
                boolean res = removeTeacherInstrument(teacherId, instrument.getId());
                response.add(res);
            }
        }

        // add new checked instruments
        for (String instrumentId : selectedInstruments) {
            int id = -1;
            try {
                id = Integer.parseInt(instrumentId);
            } catch (NumberFormatException e) {
                System.out.println("Instrument id not integer");
                continue;
            }

            boolean addInstrument = true;
            for (Instrument instrument : currentInstruments) {
                if (instrument.getId() == id) {
                    addInstrument = false;
                    break;
                }
            }
            if (addInstrument) {
                boolean res = addTeacherInstrument(teacherId, id);
                response.add(res);
            }
        }
        return response;
    }

    private boolean addTeacherInstrument(int teacherId, int instrumentId) {
        String query = """
                INSERT INTO teacher_instruments(teacher_id, instrument_id)
                VALUES (?, ?)
                """;
        Object[] args = { teacherId, instrumentId };
        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }

    private boolean removeTeacherInstrument(int teacherId, int instrumentId) {
        String query = """
                DELETE FROM teacher_instruments
                WHERE teacher_id = ?
                AND instrument_id = ?
                """;
        Object[] args = { teacherId, instrumentId };
        return Database.INSTANCE.getPreparedStatementUpdate(query, args) > 0;
    }


    /**
     * Searches for teachers based on various criteria such as instrument, skill, rating, type, and date.
     * This method returns a list of teachers who match the search criteria.
     *
     * @param instrumentId The ID of the instrument to search for. Use "%%" for all instruments.
     * @param skillId      The ID of the skill to search for. Use "%%" for all skills.
     * @param rating       The minimum rating of the teachers to include in the search results.
     * @param type         The type of teacher to search for (online or not).
     * @param date         The day of the week to search for available teachers. Use "%%" for any day.
     * @return a list of teachers who match the search criteria
     */
    public List<Teacher> searchTeachers(String instrumentId, String skillId, int rating, String type, String date) {
        StringBuilder queryBuilder = new StringBuilder("""
            SELECT DISTINCT t.*, COUNT(*) AS row_count
            FROM lesson l
            JOIN instrument i ON l.instrument_id = i.id
            JOIN teacher t ON l.teacher_id = t.id
            JOIN users u ON t.id = u.id
            LEFT JOIN teacher_schedule ts ON t.id = ts.teacher_id
            WHERE u.online::varchar ILIKE ?
              AND t.avg_rating > ?
              AND l.skill_id::varchar ILIKE ?
              AND l.instrument_id::varchar ILIKE ?
              AND (
                (ts.day::varchar ILIKE ? AND ts.id NOT IN (
                  SELECT schedule_id FROM booking WHERE is_finished = true
                ))
            """);

        if (date.equals("%%")) {
            queryBuilder.append("OR\n" +
                    "   ts.day IS NULL");
        }

        queryBuilder.append(
                ")\nGROUP BY t.id ORDER BY t.avg_rating DESC, t.id DESC;");

        Object[] args = {
                type,
                rating,
                skillId,
                instrumentId,
                date
                };
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(queryBuilder.toString(), args);
        return getTeacherList(resultSet, queryBuilder.toString());
    }

    /***
     * Gets the latitude and longitude of the given zipcode and stores them in the database
     *
     * @param zipcode the zipcode to process
     */


    private boolean processZipcode(String zipcode) {
        zipcode = zipcode.toUpperCase().replaceAll("\\s", "");
        System.out.println("PROCESSING ZIPCODE: " + zipcode);

        if (ZipcodeCoordinateDAO.INSTANCE.isZipcodeExists(zipcode)) {
            System.out.println("ZIPCODE EXISTS");
            return true;
        }
        try {
            JSONObject result = NominatimAPI.search(zipcode).getJSONObject(0);
            double latitude = result.getDouble("lat");
            double longitude = result.getDouble("lon");

            System.out.println("INSERTING NEW ZIPCODE");
            boolean res = ZipcodeCoordinateDAO.INSTANCE.insertZipcodeCoordinate(new ZipcodeCoordinate(zipcode, latitude, longitude));
            System.out.println("PROCESS ZIPCODE RESULT: " + res);
            return res;

        } catch (IOException e) {
            System.out.println("processZipcode fetch connection exception: " + e.getMessage());
        }
        return false;
    }
}
