package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Lesson;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class LessonDAOTest {
    LessonDAO dao = LessonDAO.INSTANCE;
    @Test
    public void checkLessonByIdTest(){
        Assertions.assertTrue(dao.checkLessonExistsById(5296));
        Assertions.assertFalse(dao.checkLessonExistsById(5295));
    }

    @Test
    public void insertDeleteLessonTest(){

        int numberLessons = dao.getNumberOfLessons();
        Lesson lesson = new Lesson(2409, 128, 117, 3, "season", "title", "instrument");
        int id = dao.insertLesson(lesson);
        int numberLessonsAfterInsertion = dao.getNumberOfLessons();
        Assertions.assertEquals(numberLessons + 1, numberLessonsAfterInsertion);
        dao.deleteLesson(id);
        int numberLessonsAfterAgainInsertion = dao.getNumberOfLessons();
        Assertions.assertEquals(numberLessons, numberLessonsAfterAgainInsertion);
    }

    @Test
    public void getLessonByStudentIdTest(){
        int studentId1 = 2430;
        Assertions.assertEquals(1, dao.getLessonsByStudentId(studentId1).size());
        int studentId2 = 3150;
        Assertions.assertEquals(2, dao.getLessonsByStudentId(studentId2).size());
        int studentId3 = 3093;
        Assertions.assertEquals(0, dao.getLessonsByStudentId(studentId3).size());
    }

    @Test
    public void getLessonByTeacherIdTest(){
        int teacherId2 = 2524;
        Assertions.assertEquals(2, dao.getLessonsByTeacherId(teacherId2).size());


    }
}
