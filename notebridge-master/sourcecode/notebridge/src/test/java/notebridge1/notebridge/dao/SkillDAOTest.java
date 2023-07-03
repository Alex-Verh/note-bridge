package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Skill;
import notebridge1.notebridge.model.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillDAOTest {
    SkillDAO dao = SkillDAO.INSTANCE;
    @Test
    void getSkills() {
        List<Skill> list =  dao.getSkills();
        Assertions.assertNotNull(list);
        assertEquals(3, list.size());
        Assertions.assertFalse(list.contains(null));
    }

    @Test
    void getSkillById() {
        Skill skill1 = dao.getSkillById(1);
        Skill skill2 = dao.getSkillById(2);
        Skill skill3 = dao.getSkillById(3);

        Assertions.assertNotNull(skill1);
        Assertions.assertNotNull(skill2);
        Assertions.assertNotNull(skill3);

        Assertions.assertEquals(skill1.getId(), 1);
        Assertions.assertEquals(skill2.getId(), 2);
        Assertions.assertEquals(skill3.getId(), 3);

        Assertions.assertEquals(skill1.getName(), "Beginner");
        Assertions.assertEquals(skill2.getName(), "Intermediate");
        Assertions.assertEquals(skill3.getName(), "Advanced");
    }
}