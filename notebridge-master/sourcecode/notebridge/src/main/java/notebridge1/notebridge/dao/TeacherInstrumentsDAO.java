package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Instrument;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.TeacherInstruments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The TeacherInstrumentsDAO class provides methods for accessing and manipulating teacher instrument data in the database.
 */
public enum TeacherInstrumentsDAO {

    INSTANCE;

    /**
     * Gets a list of all the teacher instruments
     *
     * @return list of teacher instruments
     */
    public List<TeacherInstruments> getTeacherInstruments(){

        String query= """
                SELECT *
                FROM teacher_instruments
                """;
        List<TeacherInstruments> list = new ArrayList<>();

        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            while (resultSet.next()) {
                int teacherId = resultSet.getInt(1);
                int instrumentId = resultSet.getInt(2);
                TeacherInstruments teacherInstruments = new TeacherInstruments(teacherId, instrumentId);
                list.add(teacherInstruments);

            }

        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * Gets a list of all teacher instruments with the given teacher id
     *
     * @param teacherId the teacher id
     * @return list of teacher instruments
     */
    public List<Instrument> getInstrumentsByTeacherId(int teacherId){

        String query= """
                SELECT ti.instrument_id, i.name
                FROM teacher_instruments ti, instrument i
                WHERE ti.teacher_id = ?
                AND ti.instrument_id = i.id
                """;
        List<Instrument> list = new ArrayList<>();

        try {
            ResultSet set = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{teacherId});
            while (set.next()) {
                int instrumentId = set.getInt("instrument_id");
                String instrumentName = set.getString("name");
                Instrument instrument = new Instrument(instrumentId, instrumentName);
                list.add(instrument);
            }

        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * Gets a list of all teacher instrument with the given instrument id.
     *
     * @param instrumentId the instrument id
     * @return list of teacher instruments
     */
    public List<Teacher> getTeachersByInstrumentId(int instrumentId){

        String query= """
                SELECT teacher_id
                FROM teacher_instruments
                WHERE instrument_id = ?
                """;
        ArrayList<Teacher> list = new ArrayList<>();

        try {
            ResultSet set = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{instrumentId});
            while (set.next()) {
                int id = set.getInt(1);
                list.add(TeacherDAO.INSTANCE.getTeacherById(id));
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return list;
    }
}
