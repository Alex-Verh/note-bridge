package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.TeacherInstruments;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherDAOTest {
    TeacherDAO dao = TeacherDAO.INSTANCE;

    @Test
    void testGetTeachers() {
        List<Teacher> list = dao.getTeachers();
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() == dao.getTeacherCount());
        Assertions.assertFalse(list.contains(null));
    }

    @Test
    void testGetTeacherById() {
        Teacher teacher = dao.getTeacherById(2399);
        Assertions.assertEquals(teacher.getId(), 2399);
        Assertions.assertEquals(teacher.getExperience(), "Recognize.");
        Assertions.assertEquals(teacher.getAvgRating(), 8.3);
        Assertions.assertEquals(teacher.getZipcode(), "5508EH");
        Assertions.assertEquals(teacher.getVideoPath(), "/media/2399.mp4");
        Assertions.assertNull(dao.getTeacherById(999999));
        }

    @Test
    void testInsertUpdateDeleteTeacher() {
        // TODO implement triggers in db
        Teacher teacher = new Teacher(2394, "TEST EXP", 5, "TEST12", null);
    }

    @Test
    void testCheckTeacherExistsByID() {
        Assertions.assertTrue(dao.checkTeacherExistsByID(2405));
        Assertions.assertTrue(dao.checkTeacherExistsByID(2409));
        Assertions.assertFalse(dao.checkTeacherExistsByID(-1));
        Assertions.assertFalse(dao.checkTeacherExistsByID(99999));
    }
}