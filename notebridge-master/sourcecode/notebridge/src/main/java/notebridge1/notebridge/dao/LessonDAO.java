package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.Lesson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The LessonDAO class provides methods to interact with the database and perform CRUD operations on Lesson objects.
 * It uses a singleton design pattern to ensure there is only one instance of the DAO throughout the application.
 */
public enum LessonDAO {

    INSTANCE;

    /**
     * Returns the lesson by id
     *
     * @param id id of the lesson
     * @return the lesson or null if not found
     */
    public Lesson getLessonById(int id) {
        String query = """
                SELECT l.*, i.name AS instrument_name
                FROM lesson l, instrument i
                WHERE l.id = ?
                AND l.instrument_id = i.id
                """;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
            resultSet.next();
            return extractLesson(resultSet);
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return null;
        }
    }

    /**
     * Extracts the Lesson object from the result set.
     *
     * @param resultSet the result set
     * @return the extracted Lesson object
     * @throws SQLException if a database access error occurs or the column label is not valid
     */
    private Lesson extractLesson(ResultSet resultSet) throws SQLException {
        int lessonId = resultSet.getInt("id");
        int teacherId = resultSet.getInt("teacher_id");
        double price = resultSet.getDouble("price");
        int instrumentId = resultSet.getInt("instrument_id");
        int skillId = resultSet.getInt("skill_id");
        String description = resultSet.getString("description");
        String title = resultSet.getString("title");
        String instrumentName = resultSet.getString("instrument_name");

        return new Lesson(lessonId, teacherId, price, instrumentId, skillId, description, title, instrumentName);
    }


    /**
     * Checks if the lesson exists by the given id.
     *
     * @param id id of the lesson
     * @return true if it exists
     */
    public boolean checkLessonExistsById(int id) {
        String query = """
                SELECT CASE
                WHEN EXISTS (
                SELECT *
                FROM lesson
                WHERE lesson.id = ?
                ) THEN TRUE
                ELSE FALSE
                END AS result;
                """;

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
            if (res.next()) {
                System.out.println("Checking if lesson exists: " + id);
                System.out.println("Check result: " + res.getString(1).equalsIgnoreCase("t"));
                return res.getString(1).equalsIgnoreCase("t"); //todo: get boolean instead?
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return false;
    }

    /**
     * Gets a list of all lessons.
     *
     * @return all lessons
     */
    public List<Lesson> getLessons() {
        String query = """
                SELECT l.*, i.name AS instrument_name
                FROM lesson l, instrument i
                WHERE l.instrument_id = i.id
                """;
        List<Lesson> lessonList = new ArrayList<>();
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            while (resultSet.next()) {
                Lesson lesson = extractLesson(resultSet);
                lessonList.add(lesson);
            }
            return lessonList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Inserts a lesson into the database.
     *
     * @param lesson the lesson to insert
     * @return the lessonId of the lesson or -1 on failure
     */
    public int insertLesson(Lesson lesson) {
        if (!(Security.validateTopic(lesson.getTitle()))) {
            System.out.println("Error: Title violation");
            return -1;
        }
        if (!(Security.validateLessonDescription(lesson.getDescription()))) {
            System.out.println("Error: Description violation");
            return -1;
        }
        if (!(Security.validatePrice(String.valueOf((int) lesson.getPrice())))) {
            System.out.println("Error: Price violation");
            return -1;
        }
        String query = """
                INSERT INTO lesson
                VALUES(DEFAULT, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;
        int lessonId;
        try {
            Object[] args = { lesson.getTeacherId(), lesson.getPrice(), lesson.getInstrumentId(),
                    lesson.getSkillId(), lesson.getDescription(), lesson.getTitle()};
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, args);
            resultSet.next();
            lessonId = resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return -1;
        }
        return lessonId;
    }

    /**
     * Deletes a lesson by id.
     *
     * @param id id of the lesson
     * @return true if deleting was successful
     */
    public boolean deleteLesson(int id) {
        String query = """
                DELETE FROM lesson
                WHERE id = ?;
                """;
        Object[] args = {id};
        int res = Database.INSTANCE.getPreparedStatementUpdate(query, args);

        return res > 0;
    }

    /**
     * Updates a lesson.
     *
     * @param lesson lesson containing new information
     * @return amount of rows affected, -1 on failure
     */
    public int updateLesson(Lesson lesson) {
        if (!(Security.validateTopic(lesson.getTitle())
                && Security.validateLessonDescription(lesson.getDescription())
                && Security.validatePrice(String.valueOf((int) lesson.getPrice())))) {
            System.out.println("Error: Validation violation");
            return -1;
        }
        String query = """
                UPDATE lesson
                SET teacher_id = ?
                SET price = ?
                SET instrument_id = ?
                SET skill_id = ?
                SET description = ?
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{
                lesson.getTeacherId(),
                lesson.getPrice(),
                lesson.getInstrumentId(),
                lesson.getSkillId(),
                lesson.getDescription()
        });
    }

    /**
     * Gets all lessons with the given search parameters.
     *
     * @param offset       offset i.e. what page to load of the lessons
     * @param skillId      skill id of the lesson
     * @param rating       rating of the teacher
     * @param location     location of the teacher
     * @return list of all lessons
     */
    public List<Lesson> getLessonsBySearch(int offset, String instrumentId, String skillId, int rating, String location, String availability, String type) {
        StringBuilder queryBuilder = new StringBuilder("""
            SELECT DISTINCT l.*, i.name AS instrument_name, t.avg_rating, COUNT(*) AS row_count
            FROM lesson l
            JOIN instrument i ON l.instrument_id = i.id
            JOIN teacher t ON l.teacher_id = t.id
            JOIN users u ON t.id = u.id
            LEFT JOIN teacher_schedule ts ON t.id = ts.teacher_id
            WHERE u.online::varchar ILIKE ?
              AND (u.city ILIKE ('%' || ? || '%') OR u.country ILIKE ('%' || ? || '%'))
              AND t.avg_rating > ?
              AND l.skill_id::varchar ILIKE ?
              AND l.instrument_id::varchar ILIKE ?
              AND (
                (ts.day::varchar ILIKE ? AND ts.id NOT IN (
                  SELECT schedule_id FROM booking WHERE is_finished = true
                ))
            """);

        if (availability.equals("%%")) {
            queryBuilder.append("OR\n" +
                    "   ts.day IS NULL");
        }

        queryBuilder.append(
                ")\nGROUP BY l.id, i.name, t.avg_rating ORDER BY t.avg_rating DESC, l.teacher_ID DESC LIMIT 10 OFFSET ?;");


        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(queryBuilder.toString(), new Object[]{
                type,
                location,
                location,
                rating,
                skillId,
                instrumentId,
                availability,
                offset
        });
        return getLessonArrayList(resultSet);
    }

    private ArrayList<Lesson> getLessonArrayList(ResultSet resultSet) {
        ArrayList<Lesson> result = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Lesson lesson = extractLesson(resultSet);

                boolean newLesson = true;
                for (Lesson entry : result) {
                    if (entry.getId() == lesson.getId()) {
                        newLesson = false;
                        break;
                    }
                }

                if (newLesson) {
                    System.out.printf("TEACHER %s LESSON ID %s %n", lesson.getTeacherId(), lesson.getId());
                    result.add(lesson);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * Gets the total number of lessons.
     *
     * @return number of lessons
     */
    public int getNumberOfLessons() {
        String query = "SELECT count(*) FROM lesson";
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
     * Retrieves all lessons for a given student.
     *
     * @param studentId the student id
     * @return list of lessons
     */
    public List<Lesson> getLessonsByStudentId(int studentId) {
        String query = """
                SELECT l.*, i.name AS instrument_name
                FROM lesson l JOIN booking b ON l.id = b.lesson_id, instrument i
                WHERE b.student_id = ?
                AND l.instrument_id = i.id
                AND b.is_finished = true
                """;
        return getLessonArrayList(Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{studentId}));
    }

    /**
     * Retrieves all lessons for a given teacher.
     *
     * @param teacherId the teacher id
     * @return list of lessons
     */
    public List<Lesson> getLessonsByTeacherId(int teacherId) {
        String query = """
                SELECT l.*, i.name AS instrument_name
                FROM lesson l, instrument i
                WHERE l.teacher_id = ?
                AND l.instrument_id = i.id
                """;
        return getLessonArrayList(Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{teacherId}));
    }
}
