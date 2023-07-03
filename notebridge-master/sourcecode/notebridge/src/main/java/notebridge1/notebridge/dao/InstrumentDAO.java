package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Instrument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The InstrumentDAO class provides methods for accessing and manipulating instrument data in the database.
 */
public enum InstrumentDAO {
    INSTANCE;

    /**
     * Retrieves the list of instruments from the database.
     * Populates the 'instruments' list with Instrument objects.
     * Prints the ID and the name of each instrument.
     *
     * @return A list of instruments retrieved from the database.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public List<Instrument> getInstruments() {
        List<Instrument> instrumentList = new ArrayList<>();

        String query = "SELECT * FROM instrument";

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);
            while (res.next()) {
                int instrumentId = res.getInt(1);
                String name = res.getString(2);

                Instrument instrument = new Instrument(instrumentId, name);
                instrumentList.add(instrument);
            }
            return instrumentList;

        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves an instrument by ID from the database.
     *
     * @param id The ID of the instrument.
     * @return The Instrument object with the specified ID, or null if not found.
     */
    public Instrument getInstrumentById(int id) {
        String query = """
                SELECT *
                FROM instrument
                WHERE id = ?
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            if (res.next()) {
                int instrumentId = res.getInt("id");
                String name = res.getString("name");
                return new Instrument(instrumentId, name);
            }
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
        return null;
    }

    /**
     * Returns the number of instruments in the database.
     *
     * @return The number of instruments.
     */
    public int getInstrumentCount() {
        String query = """
                SELECT COUNT(*)
                FROM instrument
                """;
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Inserts a new instrument into the database.
     * Checks if the instrument already exists by name before inserting.
     *
     * @param instrument The instrument to be inserted.
     * @return The ID of the inserted instrument, or -1 on failure.
     */
    public int insertInstrument(Instrument instrument) {
        String query = "INSERT INTO instrument VALUES (DEFAULT, ?) RETURNING id";
        Object[] args = {instrument.getName()};
        ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query, args);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return -1;
        }
    }

    /**
     * Checks if an instrument with the specified name already exists in the database.
     *
     * @param name The instrument name to be checked.
     * @return {@code true} if an instrument with the specified name exists, {@code false} otherwise.
     */
    public boolean checkInstrumentExistsByName(String name) {
        String query = """
                SELECT CASE
                WHEN EXISTS (
                  SELECT *
                  FROM instrument
                  WHERE LOWER(instrument.name) = LOWER(?)
                ) THEN TRUE
                ELSE FALSE
                END AS result""";

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{name});
            if (res.next()) {
                System.out.println("Checking if instrument name exists: " + name);
                System.out.println("Check result: " + res.getString(1).equalsIgnoreCase("t"));
                return res.getString(1).equalsIgnoreCase("t");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }
        return false;
    }

    /**
     * Deletes an instrument from the database based on the specified instrument ID.
     *
     * @param instrumentId The ID of the instrument to delete.
     * @return The number of rows affected.
     */
    public int deleteInstrument(int instrumentId) {
        String query = "DELETE FROM instrument WHERE id = ?";
        Object[] args = {instrumentId};

        return Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }

    /**
     * Updates an existing instrument in the database with the provided instrument information.
     *
     * @param newInstrument The new instrument object containing the updated instrument information.
     * @return The number of rows affected.
     * @throws RuntimeException If an error occurs while updating the instrument.
     */
    public int updateInstrument(Instrument newInstrument) {
        String query = """
                UPDATE instrument
                SET name = ?
                WHERE id = ?;
                """;
        Object[] args = {newInstrument.getName(), newInstrument.getId()};
        return Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }

    /**
     * Retrieves the number of instruments in the database.
     *
     * @return The number of instruments.
     */
    public int getNumberOfInstruments() {
        String query = "SELECT count(*) FROM instrument";
        int numberOfRows = -1;
        try {
            ResultSet resultSet = Database.INSTANCE.getPreparedStatementQuery(query);
            resultSet.next();
            numberOfRows = resultSet.getInt(1);


        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);

        }
        return numberOfRows;

    }

    /**
     * Retrieves the list of instruments learned by a student from the database.
     *
     * @param studentId The ID of the student.
     * @return A list of instruments learned by the student.
     */
    public List<Instrument> getInstrumentsLearnedByStudent(int studentId) {
        String query = """
                SELECT i.id, i.name
                FROM instrument i
                         JOIN lesson l ON i.id = l.instrument_id
                         JOIN booking b ON l.id = b.lesson_id
                         JOIN teacher_schedule ts on b.schedule_id = ts.id
                WHERE b.student_id = ? AND ts.day < current_timestamp AND b.is_finished = true;
                                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{studentId});
        List<Instrument> instrumentList = new ArrayList<>();
        try {
            while (res.next()) {
                int instrumentId = res.getInt(1);
                String name = res.getString(2);

                Instrument instrument = new Instrument(instrumentId, name);
                instrumentList.add(instrument);
            }
            return instrumentList;
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
    }
} 
