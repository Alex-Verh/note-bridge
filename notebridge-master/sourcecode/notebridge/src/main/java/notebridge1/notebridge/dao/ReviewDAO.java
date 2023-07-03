package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This enum class provides data access methods for managing reviews in the database.
 */
public enum ReviewDAO {
    INSTANCE;

    /**
     * Retrieves all reviews.
     *
     * @return A list of Review objects.
     */
    public List<Review> getReviews() {
        String query = """
                SELECT r.*, u.full_name AS student_name, u.city AS student_city
                FROM review r, users u
                WHERE r.student_id = u.id
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);

        List<Review> reviewList = new ArrayList<>();
        try {
            while (res.next()) {
                Review review = extractReview(res);
                reviewList.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query getReviews(): " + e.getMessage());
        }
        return reviewList;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id The ID of the review.
     * @return The Review object, or null if it does not exist.
     */
    public Review getReviewById(int id) {
        String query = """
                SELECT r.*, u.full_name AS student_name, u.city AS student_city
                FROM review r, users u
                WHERE r.id = ?
                AND r.student_id = u.id
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});

        try {
            res.next();
            return extractReview(res);
        } catch (SQLException e) {
            System.out.println("Error executing query getReviews(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns all reviews of a specific teacher.
     *
     * @param id The ID of the teacher.
     * @return A list of Review objects.
     */
    public List<Review> getReviewsOfTeacher(int id) {
        String query = """
                SELECT r.*, u.full_name AS student_name, u.city AS student_city
                FROM review r, users u
                WHERE r.teacher_id = ?
                AND r.student_id = u.id
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        List<Review> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Review review = extractReview(resultSet);
                list.add(review);
            }
            return list;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Returns all reviews written by a specific student.
     *
     * @param id The ID of the student.
     * @return A list of Review objects.
     */
    public List<Review> getReviewsOfStudent(int id) {
        String query = """
                SELECT r.*, u.full_name AS student_name, u.city AS student_city
                FROM review r, users u
                WHERE r.student_id = ?
                AND r.student_id = u.id
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        List<Review> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Review review = extractReview(resultSet);
                list.add(review);
            }
            return list;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Extracts a Review object from the given ResultSet.
     *
     * @param res The ResultSet containing the review data.
     * @return A Review object.
     * @throws SQLException If an SQL error occurs.
     */
    private Review extractReview(ResultSet res) throws SQLException {
        int reviewId = res.getInt("id");
        int teacherId = res.getInt("teacher_id");
        int studentId = res.getInt("student_id");
        double rating = res.getDouble("rating");
        String comment = res.getString("comment");
        int lessonId = res.getInt("lesson_id");
        String studentName = res.getString("student_name");
        String studentCity = res.getString("student_city");

        return new Review(reviewId, teacherId, studentId, rating, comment, lessonId, studentName, studentCity);
    }

    /**
     * Adds a review connected to a teacher.
     *
     * @param review The review to add.
     * @return The ID of the review.
     */
    public int addReviewToTeacher(Review review) {
        if (!(Security.validateMark(String.valueOf(review.getRating())) && Security.validateMessage(review.getComment()))) {
            System.out.println("Error: Validation violation");
            return -1;
        }
        String query = """
                INSERT INTO review
                VALUES(DEFAULT, ?, ?, ?, ?, ?)
                RETURNING id;
                """;
        Object[] args = {review.getTeacherId(), review.getStudentId(), review.getRating(), review.getComment(), review.getLessonId()};
        ResultSet result = Database.INSTANCE.getPreparedStatementQuery(query, args);
        int id = -1;
        try {
            result.next();
            id = result.getInt(1);
            return id;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return id;
        }
    }

    /**
     * Returns the count of reviews for a specific teacher.
     *
     * @param id The ID of the teacher.
     * @return The count of reviews.
     */
    public int getCountOfTeacher(int id) {
        String query = """
                SELECT COUNT(*)
                FROM review
                WHERE teacher_id = ?
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Returns the count of reviews for a specific student.
     *
     * @param id The ID of the student.
     * @return The count of reviews.
     */
    public int getCountOfStudent(int id) {
        String query = """
                SELECT COUNT(*)
                FROM review
                WHERE student_id = ?
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Returns the count of reviews for a specific lesson from a student.
     *
     * @param lessonId  The ID of the lesson.
     * @param studentId The ID of the student.
     * @return The count of reviews.
     */
    public int getCountReviewsForLessonFromStudent(int lessonId, int studentId) {
        String query = """
                SELECT COUNT(*)
                FROM review
                WHERE lesson_id = ?
                AND student_id = ?
                GROUP BY (id)
                """;
        Object[] args = { lessonId, studentId };
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, args);
        int res = 0;

        try {
            if (resultSet.next()) {
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
        }
        return res;
    }


    /**
     * Deletes a review for a specific lesson and student.
     *
     * @param lessonId  The ID of the lesson.
     * @param studentId The ID of the student.
     * @return The number of affected rows.
     */
    public int deleteReview(int lessonId, int studentId) {
        String query = """
                DELETE
                FROM review
                WHERE lesson_id = ?
                AND student_id = ?
                """;
        Object[] args = { lessonId, studentId };
        return Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }
}
