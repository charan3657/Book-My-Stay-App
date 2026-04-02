import java.util.*;

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

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room Type: " + roomType;
    }
}

// Booking History (Stores confirmed bookings)
class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        return confirmedBookings;
    }

    // Display history
    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        if (confirmedBookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : confirmedBookings) {
            System.out.println(r);
        }
    }
}

// Reporting Service
class BookingReportService {

    public void generateSummary(BookingHistory history) {
        System.out.println("\n=== Booking Summary Report ===");

        List<Reservation> list = history.getAllReservations();

        if (list.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : list) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + " Bookings: " + entry.getValue());
        }

        System.out.println("Total Bookings: " + list.size());
    }
}

// Inventory Service
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void reduceAvailability(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\n=== Updated Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> Available: " + entry.getValue());
        }
    }
}

// Booking Service
class BookingService {

    private Queue<Reservation> bookingQueue;
    private RoomInventory inventory;
    private BookingHistory history;

    public BookingService(Queue<Reservation> bookingQueue,
                          RoomInventory inventory,
                          BookingHistory history) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.history = history;
    }

    public void processBookings() {

        System.out.println("=== Processing Bookings ===\n");

        while (!bookingQueue.isEmpty()) {

            Reservation request = bookingQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("Processing: " + request.getGuestName());

            if (inventory.getAvailability(roomType) > 0) {

                inventory.reduceAvailability(roomType);

                // ✅ Add to history ONLY if confirmed
                history.addReservation(request);

                System.out.println("Booking Confirmed for " + request.getGuestName());

            } else {
                System.out.println("Booking Failed for " + request.getGuestName());
            }
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=== Book My Stay App v8.0 ===\n");

        // Step 1: Queue
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Single Room"));
        queue.offer(new Reservation("Charlie", "Single Room")); // fail
        queue.offer(new Reservation("David", "Suite Room"));

        // Step 2: Services
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(queue, inventory, history);

        // Step 3: Process
        bookingService.processBookings();

        // Step 4: View History
        history.displayHistory();

        // Step 5: Reporting
        BookingReportService reportService = new BookingReportService();
        reportService.generateSummary(history);

        // Step 6: Inventory
        inventory.displayInventory();

        System.out.println("\nSystem ready for admin reporting.");
    }
}