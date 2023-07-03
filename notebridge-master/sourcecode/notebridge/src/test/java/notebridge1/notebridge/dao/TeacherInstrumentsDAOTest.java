package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Instrument;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.TeacherInstruments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherInstrumentsDAOTest {
    TeacherInstrumentsDAO dao = TeacherInstrumentsDAO.INSTANCE;
    @Test
    void testGetTeacherInstruments() {
        List<TeacherInstruments> list = dao.getTeacherInstruments();
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() > 140);
        Assertions.assertFalse(list.contains(null));
    }

    @Test
    void testGetInstrumentsByTeacherId() {
        List<Instrument> list = dao.getInstrumentsByTeacherId(2399);
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.contains(null));
        assertEquals(4, list.size());
        Assertions.assertFalse(list.contains(null));
        list.sort(new Comparator<Instrument>() {
            @Override
            public int compare(Instrument o1, Instrument o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        assertEquals(116, list.get(0).getId());
        assertEquals(121, list.get(1).getId());
    }

    @Test
    void testGetTeachersByInstrumentId() {
        List<Teacher> list = dao.getTeachersByInstrumentId(114);
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.contains(null));
        Assertions.assertEquals(list.size(), 81);
    }
}