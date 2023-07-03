package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.ZipcodeCoordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZipcodeCoordinateDAOTest {
    ZipcodeCoordinateDAO dao = ZipcodeCoordinateDAO.INSTANCE;
    @Test
    void testGetZipcodeCoordinates() {
        List<ZipcodeCoordinate> list = dao.getZipcodeCoordinates();
        Assertions.assertNotNull(list);
        for (ZipcodeCoordinate zipcodeCoordinate: list) {
            Assertions.assertNotNull(zipcodeCoordinate);
        }
    }

    @Test
    void testGetZipcodeByName() {
        ZipcodeCoordinate zipcodeCoordinate = dao.getZipcodeByName("7545ZP");
        Assertions.assertNotNull(zipcodeCoordinate);
        Assertions.assertEquals(zipcodeCoordinate.getZipcode(),"7545ZP");
        Assertions.assertEquals(zipcodeCoordinate.getLongitude(), 6.8590224666666700);
        Assertions.assertEquals(zipcodeCoordinate.getLatitude(), 52.2171067285714000);
    }

    @Test
    void testIsZipcodeExists() {
        Assertions.assertTrue(dao.isZipcodeExists("7545ZP"));
        Assertions.assertTrue(dao.isZipcodeExists("7545zP"));
        Assertions.assertTrue(dao.isZipcodeExists("7545 ZP"));
        Assertions.assertFalse(dao.isZipcodeExists("000000"));
    }

    @Test
    void testInsertZipcodeCoordinate() {
        if (dao.isZipcodeExists("te st")) {
            String query = """
                DELETE FROM zipcode_coordinates
                WHERE zipcode = ?
                """;
            Object[] args = {"TEST"};

            Database.INSTANCE.getPreparedStatementUpdate(query, args);
        }
        dao.insertZipcodeCoordinate(new ZipcodeCoordinate("te st", 0,0));
        ZipcodeCoordinate zipcodeCoordinateFromDb = dao.getZipcodeByName("test");
        Assertions.assertNotNull(zipcodeCoordinateFromDb);
        Assertions.assertEquals(zipcodeCoordinateFromDb.getZipcode(), "TEST");
        Assertions.assertEquals(zipcodeCoordinateFromDb.getLatitude(), 0);
        Assertions.assertEquals(zipcodeCoordinateFromDb.getLongitude(), 0);
    }

    @Test
    void updateZipcodeCoordinate() {
        ZipcodeCoordinate zipcodeCoordinate = dao.getZipcodeByName("te st");
        String zipcode = zipcodeCoordinate.getZipcode();
        double latitude = zipcodeCoordinate.getLatitude();
        double longitude = zipcodeCoordinate.getLongitude();
        dao.updateZipcodeCoordinate(new ZipcodeCoordinate("te st", latitude+1, longitude+1));
        zipcodeCoordinate = dao.getZipcodeByName("te st");

        Assertions.assertNotNull(zipcodeCoordinate);
        Assertions.assertEquals(zipcodeCoordinate.getLongitude(), latitude+1);
        Assertions.assertEquals(zipcodeCoordinate.getLatitude(), latitude+1);
        Assertions.assertEquals(zipcodeCoordinate.getZipcode(), zipcode);
    }
}