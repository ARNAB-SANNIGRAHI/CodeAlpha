import java.io.Serializable;
import java.time.LocalDate;

public class Reservation implements Serializable {
    private String reservationId;
    private String userName;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean paymentStatus;

    public Reservation(String reservationId, String userName, Room room, LocalDate checkInDate, LocalDate checkOutDate, boolean paymentStatus) {
        this.reservationId = reservationId;
        this.userName = userName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.paymentStatus = paymentStatus;
    }

    public String getReservationId() { return reservationId; }
    public String getUserName() { return userName; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(boolean paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + ", User: " + userName + ", Room: " + room + ", Check-in: " + checkInDate + ", Check-out: " + checkOutDate + ", Paid: " + paymentStatus;
    }
} 