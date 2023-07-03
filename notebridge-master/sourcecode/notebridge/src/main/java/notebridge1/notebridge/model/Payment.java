package notebridge1.notebridge.model;

import java.sql.Date;

public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private Date paymentTimestamp;
    private boolean status;

    public Payment() {
    }

    public Payment(double amount) {
        this.amount = amount;
    }

    public Payment(int bookingId, double amount, Date paymentTimestamp, boolean status) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentTimestamp = paymentTimestamp;
        this.status = status;
    }

    public Payment(int paymentId, int bookingId, double amount, Date paymentTimestamp, boolean status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentTimestamp = paymentTimestamp;
        this.status = status;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(Date paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
