package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.dao.InstrumentDAO;
import notebridge1.notebridge.model.Instrument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstrumentDAOTest {

    InstrumentDAO dao = InstrumentDAO.INSTANCE;
    @Test
    public void insertDeleteInstrumentTest(){
        if(!dao.checkInstrumentExistsByName("Clap Box")) {
            int numberInstruments = dao.getNumberOfInstruments();
            Instrument instrument = new Instrument(7, "Clap Box");
            int instrumentId = dao.insertInstrument(instrument);
            int numberInstrumentsAfterInsertion = dao.getNumberOfInstruments();
            Assertions.assertEquals(numberInstruments + 1, numberInstrumentsAfterInsertion);

            dao.deleteInstrument(instrumentId);
            int numberInstrumentsAfterDeletion = dao.getNumberOfInstruments();
            Assertions.assertEquals(numberInstruments, numberInstrumentsAfterDeletion);
        }

    }

    @Test
    public void checkInstrumentByNameTest(){
        Assertions.assertTrue(dao.checkInstrumentExistsByName("Bugle"));
        Assertions.assertFalse(dao.checkInstrumentExistsByName("Electric Guitar"));
    }

    @Test
    public void UpdateInstrumentTest(){
        Instrument instrument = new Instrument(119, "Classic Drums");
        dao.updateInstrument(instrument);
        Assertions.assertEquals("Classic Drums", dao.getInstrumentById(119).getName() );

        Instrument instrument2 = new Instrument(119, "Drums");
        dao.updateInstrument(instrument2);
        Assertions.assertEquals("Drums", dao.getInstrumentById(119).getName());

    }
}
