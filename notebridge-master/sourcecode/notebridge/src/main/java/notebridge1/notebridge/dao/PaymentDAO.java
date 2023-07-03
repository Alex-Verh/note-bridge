package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;

/**
 * This enum class provides data access methods for managing payments in the database.
 */
public enum PaymentDAO {
    INSTANCE;

    /**
     * Sets the payment status with the given ID to true, indicating that it has been paid.
     *
     * @param id The ID of the payment.
     * @return The number of rows affected in the database, or -1 if the update failed.
     */
    public int updatePaymentToPayed(int id) {
        String query = """
                UPDATE payment
                SET status = true, payment_timestamp = current_timestamp
                WHERE id = ?;
                """;
        return Database.INSTANCE.getPreparedStatementUpdate(query, new Object[]{id});
    }
}
