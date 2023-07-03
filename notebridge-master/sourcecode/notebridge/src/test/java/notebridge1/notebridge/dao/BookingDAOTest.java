package notebridge1.notebridge.dao;

import notebridge1.notebridge.model.Booking;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BookingDAOTest {
    BookingDAO dao = BookingDAO.INSTANCE;

    @Test
    public void insertDeleteBookingTest() {

        int beforeInsertionNumber = dao.getNumberOfBookings();
        Booking booking = new Booking(2405, 5708, 9519, false, true);

        int idBooking = dao.insertBookingAlongWithPayment(booking);
        Assertions.assertEquals(beforeInsertionNumber + 1, dao.getNumberOfBookings());
        dao.deleteBookingById(idBooking);
        Assertions.assertEquals(beforeInsertionNumber, dao.getNumberOfBookings());

    }
}
