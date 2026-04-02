import java.util.*;

// ✅ Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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
        return guestName + " - " + roomType;
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation r) {
        confirmedBookings.add(r);
    }

    public List<Reservation> getAllReservations() {
        return confirmedBookings;
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : confirmedBookings) {
            System.out.println(r);
        }
    }
}

// Reporting Service
class BookingReportService {
    public void generateSummary(BookingHistory history) {
        System.out.println("\n=== Booking Report ===");

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : history.getAllReservations()) {
            countMap.put(
                    r.getRoomType(),
                    countMap.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// Inventory Service
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, -1); // -1 = invalid type
    }

    public void reduceAvailability(String roomType) throws InvalidBookingException {
        int available = getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No availability for " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public void displayInventory() {
        System.out.println("\n=== Inventory ===");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// ✅ Validator Class (Fail-Fast)
class BookingValidator {

    public static void validate(Reservation r, RoomInventory inventory)
            throws InvalidBookingException {

        if (r.getGuestName() == null || r.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new InvalidBookingException("Room not available: " + r.getRoomType());
        }
    }
}

// Booking Service
class BookingService {

    private Queue<Reservation> queue;
    private RoomInventory inventory;
    private BookingHistory history;

    public BookingService(Queue<Reservation> queue,
                          RoomInventory inventory,
                          BookingHistory history) {
        this.queue = queue;
        this.inventory = inventory;
        this.history = history;
    }

    public void processBookings() {

        System.out.println("=== Processing Bookings ===\n");

        while (!queue.isEmpty()) {

            Reservation r = queue.poll();

            try {
                // ✅ VALIDATION FIRST (Fail-Fast)
                BookingValidator.validate(r, inventory);

                // ✅ Only executed if valid
                inventory.reduceAvailability(r.getRoomType());
                history.addReservation(r);

                System.out.println("Booking Confirmed: " + r);

            } catch (InvalidBookingException e) {
                // ✅ Graceful error handling
                System.out.println("Booking Failed for " + r.getGuestName()
                        + " → " + e.getMessage());
            }
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=== Book My Stay App v9.0 ===\n");

        Queue<Reservation> queue = new LinkedList<>();

        // ✅ Valid bookings
        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Double Room"));

        // ❌ Invalid cases
        queue.offer(new Reservation("", "Single Room"));              // empty name
        queue.offer(new Reservation("Charlie", "King Room"));         // invalid type
        queue.offer(new Reservation("David", "Single Room"));         // may fail if full
        queue.offer(new Reservation("Eve", "Single Room"));           // overbooking

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        BookingService service = new BookingService(queue, inventory, history);

        service.processBookings();

        history.displayHistory();

        BookingReportService report = new BookingReportService();
        report.generateSummary(history);

        inventory.displayInventory();

        System.out.println("\nSystem handled all errors safely.");
    }
}