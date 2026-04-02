import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation Class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId; // NEW (for cancellation tracking)

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }

    @Override
    public String toString() {
        return guestName + " - " + roomType + " (" + roomId + ")";
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> confirmed = new ArrayList<>();

    public void add(Reservation r) {
        confirmed.add(r);
    }

    public boolean remove(String guestName) {
        return confirmed.removeIf(r -> r.getGuestName().equals(guestName));
    }

    public Reservation find(String guestName) {
        for (Reservation r : confirmed) {
            if (r.getGuestName().equals(guestName)) return r;
        }
        return null;
    }

    public void display() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : confirmed) {
            System.out.println(r);
        }
    }
}

// Inventory
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, -1);
    }

    public boolean isValid(String type) {
        return inventory.containsKey(type);
    }

    public void reduce(String type) throws InvalidBookingException {
        int val = getAvailability(type);
        if (val <= 0) throw new InvalidBookingException("No availability");
        inventory.put(type, val - 1);
    }

    public void increase(String type) {
        inventory.put(type, inventory.get(type) + 1);
    }

    public void display() {
        System.out.println("\n=== Inventory ===");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// Validator
class BookingValidator {
    public static void validate(Reservation r, RoomInventory inv)
            throws InvalidBookingException {

        if (r.getGuestName() == null || r.getGuestName().isEmpty())
            throw new InvalidBookingException("Invalid guest name");

        if (!inv.isValid(r.getRoomType()))
            throw new InvalidBookingException("Invalid room type");

        if (inv.getAvailability(r.getRoomType()) <= 0)
            throw new InvalidBookingException("Room not available");
    }
}

// Booking Service
class BookingService {

    private Queue<Reservation> queue;
    private RoomInventory inventory;
    private BookingHistory history;

    private int counter = 1;

    public BookingService(Queue<Reservation> q, RoomInventory i, BookingHistory h) {
        queue = q;
        inventory = i;
        history = h;
    }

    public void process() {

        while (!queue.isEmpty()) {

            Reservation r = queue.poll();

            try {
                BookingValidator.validate(r, inventory);

                inventory.reduce(r.getRoomType());

                String roomId = generateId(r.getRoomType());
                r.setRoomId(roomId);

                history.add(r);

                System.out.println("Confirmed: " + r);

            } catch (InvalidBookingException e) {
                System.out.println("Failed: " + r.getGuestName() + " → " + e.getMessage());
            }
        }
    }

    private String generateId(String type) {
        return type.substring(0, 2).toUpperCase() + counter++;
    }
}

// ✅ Cancellation Service (NEW)
class CancellationService {

    private BookingHistory history;
    private RoomInventory inventory;

    // Stack for rollback
    private Stack<String> releasedRoomIds = new Stack<>();

    public CancellationService(BookingHistory h, RoomInventory i) {
        history = h;
        inventory = i;
    }

    public void cancel(String guestName) {

        Reservation r = history.find(guestName);

        if (r == null) {
            System.out.println("Cancellation Failed: No booking found for " + guestName);
            return;
        }

        // LIFO rollback tracking
        releasedRoomIds.push(r.getRoomId());

        // Restore inventory
        inventory.increase(r.getRoomType());

        // Remove from history
        history.remove(guestName);

        System.out.println("Cancelled booking for " + guestName +
                ", Room Released: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack: " + releasedRoomIds);
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Single Room"));
        queue.offer(new Reservation("Charlie", "Suite Room"));

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        BookingService service = new BookingService(queue, inventory, history);
        service.process();

        history.display();
        inventory.display();

        // ✅ Cancellation
        CancellationService cancelService = new CancellationService(history, inventory);

        System.out.println("\n=== Cancellation Phase ===");

        cancelService.cancel("Bob");       // valid
        cancelService.cancel("Unknown");   // invalid

        history.display();
        inventory.display();

        cancelService.showRollbackStack();
    }
}