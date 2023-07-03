package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Review;
import notebridge1.notebridge.model.Skill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOTest {
    ReviewDAO dao = ReviewDAO.INSTANCE;
    @Test
    void testGetReviews() {
        List<Review> list =  dao.getReviews();
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() > 90);
        Assertions.assertFalse(list.contains(null));
    }

    @Test
    void testGetReviewsOfTeacher() {
        List<Review> list =  dao.getReviewsOfTeacher(2399);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        Assertions.assertFalse(list.contains(null));
        for(Review review: list) {
            Assertions.assertEquals(review.getTeacherId(), 2399);
        }
    }

    @Test
    void testAddDeleteReviewFromTeacher() {
        Review review = new Review(2954, 2417, 10, "TEST", 5917);
        int nrOfReviewsBefore = dao.getCountOfTeacher(2954);
        int id = dao.addReviewToTeacher(review);
        Review reviewFromDb = dao.getReviewById(id);
        // Check if review was inserted properly
        Assertions.assertEquals(dao.getCountOfTeacher(2954), nrOfReviewsBefore + 1);
        Assertions.assertNotNull(reviewFromDb);
        Assertions.assertEquals(reviewFromDb.getRating(),review.getRating());
        Assertions.assertEquals(reviewFromDb.getId(),id);
        Assertions.assertEquals(reviewFromDb.getComment(),review.getComment());
        Assertions.assertEquals(reviewFromDb.getStudentId(),review.getStudentId());
        Assertions.assertEquals(reviewFromDb.getTeacherId(),review.getTeacherId());
        // Check if review was deleted properly
        assertEquals(1, dao.deleteReview(5917, 2417));
        Assertions.assertEquals(nrOfReviewsBefore, dao.getCountOfTeacher(2954));
        Assertions.assertNull(dao.getReviewById(id));
    }

    @Test
    void testGetCountOfTeacher() {
        Assertions.assertEquals(dao.getCountOfTeacher(2399), 2);
        Assertions.assertEquals(dao.getCountOfTeacher(-1), 0);
    }

    @Test
    void getReviewById() {
        // Checking an existing review
        Review review = dao.getReviewById(3083);
        Assertions.assertNotNull(review);
        Assertions.assertEquals(review.getRating(),9.9);
        Assertions.assertEquals(review.getId(),3083);
        Assertions.assertEquals(review.getComment(),"Owner future care recent eight southern involve. According least court debate.\n" +
                "Series tax plan past law any air.");
        Assertions.assertEquals(review.getStudentId(),4145);
        Assertions.assertEquals(review.getTeacherId(),2399);
        // Checking a non-existent review
        Assertions.assertNull(dao.getReviewById(-1));
    }

    @Test
    void testGetReviewsOfStudent() {
        List<Review> list =  dao.getReviewsOfStudent(2630);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        Assertions.assertFalse(list.contains(null));
        for(Review review: list) {
            Assertions.assertEquals(review.getStudentId(), 2630);
        }
    }

    @Test
    void testGetCountOfStudent() {
        Assertions.assertEquals(dao.getCountOfStudent(2630), 2);
        Assertions.assertEquals(dao.getCountOfStudent(-1), 0);
    }
}