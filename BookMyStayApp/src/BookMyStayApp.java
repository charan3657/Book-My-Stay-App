import java.util.LinkedList;
import java.util.Queue;

/**
 * UseCase5BookingRequestQueue
 *
 * Demonstrates handling of booking requests using a Queue (FIFO).
 * Requests are stored in arrival order without modifying inventory.
 *
 * @author YourName
 * @version 5.0
 */

// Reservation Class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void display() {
        System.out.println("Guest: " + guestName + ", Room Type: " + roomType);
    }
}

// Booking Queue Class
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request to queue
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Booking request added:");
        reservation.display();
    }

    // Display all queued requests
    public void displayQueue() {
        System.out.println("\n=== Booking Request Queue ===");
        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : queue) {
            r.display();
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Welcome to Book My Stay App!");
        System.out.println("Hotel Booking System v5.0\n");

        // Initialize queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Simulate booking requests (arrival order)
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Display queue
        bookingQueue.displayQueue();

        System.out.println("\nAll requests stored in FIFO order.");
        System.out.println("No inventory updates performed.");
    }
}