package notebridge1.notebridge;

import java.sql.*;

/**
 * The Database enum provides a singleton instance for managing the database connection and executing SQL queries.
 * It uses JDBC to connect to the database and provides methods to execute update and query operations.
 */
public enum Database {
    INSTANCE;

    private String url;
    private String username;
    private String password;

    private Statement statement;
    private Connection connection;

    /**
     * Constructs the Database singleton by reading the database environment variables and creating a database connection.
     */
    Database() {
        System.out.println("Constructing Database singleton...");
        ReadDBEnvironmentVariables();
        CreateConnection();
    }

    private void ReadDBEnvironmentVariables() {
        url = System.getenv("DB_URL");
        username = System.getenv("DB_USERNAME");
        password = System.getenv("DB_PASSWORD");

//        System.out.println(url);
//        System.out.println(username);
//        System.out.println(password);
    }


    private void CreateConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading the driver " + e);
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (SQLException sqlException) {
            System.out.println("Error connecting" + sqlException);
        }
    }

    /**
     * Retrieves the statement object associated with the database connection.
     *
     * @return the statement object
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Executes an SQL UPDATE, INSERT, or DELETE query using a prepared statement.
     * Statements returning a ResultSet object will result in an exception thrown.
     *
     * @param sqlQuery The SQL query to execute, with parameter placeholders represented by question marks.
     * @param args     The values to be set for the prepared statement parameters.
     * @return The number of rows affected by the query. 0 if no rows were affected or errors occurred, -1 if the number of parameters is incorrect.
     * @throws RuntimeException If an exception occurs during the execution of the query.
     */
    public int getPreparedStatementUpdate(String sqlQuery, Object[] args) {

        PreparedStatement preparedStatement;
        int affectedRows = 0;

        long parameterAmount = sqlQuery.chars().filter(ch -> ch == '?').count();
        System.out.println("Nr of expected params: " + parameterAmount);
        System.out.println("Nr of received params: " + args.length);
        for (Object obj : args) {
            System.out.println("argument: " + obj);
        }

        if (parameterAmount != args.length) {
            System.out.println("Incorrect number of parameters given!");
            return -1;
        }

        try {
            preparedStatement = connection.prepareStatement(sqlQuery);
            for (int i = 1; i <= parameterAmount; i++) {
                preparedStatement.setObject(i, args[i - 1]);
            }
            affectedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return affectedRows;
    }


    /**
     * Executes an SQL query using a prepared statement with parameters.
     *
     * @param sqlQuery The SQL query to execute.
     * @param args     An array of objects representing the parameters to be set in the prepared statement.
     * @return The result set containing the data retrieved from the query, or null if there are not enough parameters.
     */
    public ResultSet getPreparedStatementQuery(String sqlQuery, Object[] args) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        long parameterAmount = sqlQuery.chars().filter(ch -> ch == '?').count();
        if (parameterAmount != args.length) {
            System.out.println("Incorrect number of parameters given!");
        } else {
            try {
                preparedStatement = connection.prepareStatement(sqlQuery);
                for (int i = 1; i <= parameterAmount; i++) {
                    preparedStatement.setObject(i, args[i - 1]);
                }
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return resultSet;
    }

    /**
     * Executes an SQL SELECT query using a prepared statement.
     *
     * @param sqlQuery The SQL query to execute.
     * @return The result set containing the data retrieved from the query.
     * @throws RuntimeException If an exception occurs during the execution of the query.
     */
    public ResultSet getPreparedStatementQuery(String sqlQuery) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }
}
