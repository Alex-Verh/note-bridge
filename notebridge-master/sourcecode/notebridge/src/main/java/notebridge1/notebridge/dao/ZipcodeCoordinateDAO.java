package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.ZipcodeCoordinate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ZipcodeCoordinateDAO {
    INSTANCE;

    /**
     * Retrieves the details of all zipcode coordinates from the database.
     * Each zipcode coordinate's information includes their zipcode, latitude, and longitude.
     *
     * @return the list of zipcode coordinates
     */
    public List<ZipcodeCoordinate> getZipcodeCoordinates() {
        String query = "SELECT * FROM zipcode_coordinates";
        List<ZipcodeCoordinate> result = new ArrayList<>();

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);
            while (res.next()) {
                String zipcode = res.getString("zipcode");
                double latitude = res.getDouble("latitude");
                double longitude = res.getDouble("longitude");

                ZipcodeCoordinate zipcodeCoordinate = new ZipcodeCoordinate(zipcode, latitude, longitude);
                result.add(zipcodeCoordinate);
            }

        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Returns the zipcode coordinate with the given zipcode name, or null if it does not exist.
     *
     * @param zipcode the name of the zipcode
     * @return the ZipcodeCoordinate object
     */
    public ZipcodeCoordinate getZipcodeByName(String zipcode) {
        zipcode = zipcode.toUpperCase().replaceAll("\\s", "");

        if (!isZipcodeExists(zipcode)) {
            return null;
        }

        String query = """
                SELECT *
                FROM zipcode_coordinates
                WHERE REPLACE(UPPER(zipcode), ' ', '') LIKE REPLACE(UPPER(?), ' ', '');
                """;

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{ zipcode });
            if (res.next()) {
                double latitude = res.getDouble("latitude");
                double longitude = res.getDouble("longitude");
                return new ZipcodeCoordinate(zipcode, latitude, longitude);
            }
        } catch (SQLException e) {
            System.err.println("Error from query " + query + " : " + e);
            return null;
        }
        return null;
    }

    /**
     * Checks if the given zipcode is already in the database.
     *
     * @param zipcode the zipcode to check
     * @return true if the zipcode already exists
     */
    public boolean isZipcodeExists(String zipcode) {
        zipcode = zipcode.toUpperCase().replaceAll("\\s", "");

        String query = """
                SELECT COUNT(*) FROM zipcode_coordinates WHERE UPPER(zipcode) = UPPER(?)
                """;
        Object[] args = { zipcode };

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, args);
            if (res.next()) {
                return res.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("isZipcodeExists: " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserts the given values into the zipcode_coordinates table in the database.
     *
     * @param zipcodeCoordinate the ZipcodeCoordinate object to be inserted
     * @return true if the zipcode coordinate is successfully inserted, false otherwise
     */
    public boolean insertZipcodeCoordinate(ZipcodeCoordinate zipcodeCoordinate) {
        String zipcode = zipcodeCoordinate.getZipcode().toUpperCase().replaceAll("\\s", "");

        if (isZipcodeExists(zipcode)) {
            System.out.println("Zipcode already exists!");
            return false;
        }

        String query = """
                INSERT INTO zipcode_coordinates (zipcode, latitude, longitude)
                VALUES (?, ?, ?)
                """;
        Object[] args = { zipcode, zipcodeCoordinate.getLatitude(), zipcodeCoordinate.getLongitude() };
        int res = Database.INSTANCE.getPreparedStatementUpdate(query, args);

        if (res > 0) {
            System.out.printf("Zipcode successfully inserted: %s %s %s %n",
                    zipcode, zipcodeCoordinate.getLatitude(), zipcodeCoordinate.getLongitude() );
        }
        return res > 0;
    }

    /**
     * Updates the latitude and longitude values of a zipcode coordinate in the "zipcode_coordinates" table.
     *
     * @param zipcodeCoordinate the ZipcodeCoordinate object containing the updated values
     * @return the number of rows affected by the update operation. Returns -1 if the zipcode coordinate does not exist.
     */
    public int updateZipcodeCoordinate(ZipcodeCoordinate zipcodeCoordinate) {
        String zipcode = zipcodeCoordinate.getZipcode().toUpperCase().replaceAll("\\s", "");

        if (!isZipcodeExists(zipcodeCoordinate.getZipcode())) {
            System.out.println("Zipcode coordinate not found, name: " + zipcode);
            return -1;
        }

        String query = """
                UPDATE zipcode_coordinates
                SET latitude  = ?,
                    longitude = ?
                WHERE UPPER(zipcode) = UPPER(?);
                """;
        Object[] args = { zipcodeCoordinate.getLatitude(), zipcodeCoordinate.getLongitude(), zipcode };

        return Database.INSTANCE.getPreparedStatementUpdate(query, args);
    }
}
