package notebridge1.notebridge.dao;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import notebridge1.notebridge.model.TeacherSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherScheduleDAOTest {
    TeacherScheduleDAO dao = TeacherScheduleDAO.INSTANCE;
    @Test
    void testGetTeacherSchedules() {
        List<TeacherSchedule> list = dao.getTeacherSchedules();
        Assertions.assertNotNull(list);
    }

    @Test
    void testInsertDeleteTeacherSchedule() {
        TeacherSchedule teacherSchedule = new TeacherSchedule(5151, new Date(2023, 12, 1), new Time(10,0,0), new Time(12,0,0));
        int nrSchedulesBefore = dao.getTeacherSchedules().size();
        int id = dao.insertTeacherSchedule(teacherSchedule);
        TeacherSchedule teacherScheduleFromDb = dao.getTeacherScheduleById(id);
        // Testing if teacherSchedule was added properly to db
        Assertions.assertNotNull(teacherScheduleFromDb);
        Assertions.assertEquals(nrSchedulesBefore+1, dao.getTeacherSchedules().size());
        Assertions.assertEquals(id, teacherScheduleFromDb.getId());
        Assertions.assertEquals(teacherSchedule.getTeacherId(), teacherScheduleFromDb.getTeacherId());
        Assertions.assertEquals(teacherSchedule.getDate(), teacherScheduleFromDb.getDate());
        Assertions.assertEquals(teacherSchedule.getStartTime(), teacherScheduleFromDb.getStartTime());
        Assertions.assertEquals(teacherSchedule.getEndTime(), teacherScheduleFromDb.getEndTime());
        // Testing if teacherSchedule was deleted properly from db
        Assertions.assertEquals(dao.deleteTeacherSchedule(id), 1);
        Assertions.assertEquals(nrSchedulesBefore, dao.getTeacherSchedules().size());
    }

    @Test
    void testGetTeacherSchedulesById() {
        TeacherSchedule teacherSchedule = dao.getTeacherScheduleById(8052);
        Assertions.assertNotNull(teacherSchedule);
        Assertions.assertEquals(teacherSchedule.getId(), 8052);
        Assertions.assertEquals(teacherSchedule.getTeacherId(), 2616);
        Assertions.assertEquals(teacherSchedule.getDate().toString(), "2023-09-29");
        Assertions.assertEquals(teacherSchedule.getStartTime().toString(), "03:25:19");
        Assertions.assertEquals(teacherSchedule.getEndTime().toString(), "04:22:19");
    }

    @Test
    void testGetTeacherScheduleById() {
        List<TeacherSchedule> list = dao.getFreeTeacherSchedulesByTeacherId(2414);
        Assertions.assertNotNull(list);
        for(TeacherSchedule teacherSchedule: list) {
            Assertions.assertNotNull(teacherSchedule);
            Assertions.assertEquals(teacherSchedule.getTeacherId(), 2414);
            Assertions.assertNotNull(teacherSchedule.getStartTime());
            Assertions.assertNotNull(teacherSchedule.getEndTime());
        }
    }
}