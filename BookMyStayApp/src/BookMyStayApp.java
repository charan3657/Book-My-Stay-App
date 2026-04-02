import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return guestName + " - " + roomType;
    }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> bookings = new ArrayList<>();

    public void add(Reservation r) {
        bookings.add(r);
    }

    public List<Reservation> getAll() {
        return bookings;
    }

    public void display() {
        System.out.println("\n=== Booking History ===");
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        }
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }
}

// Inventory (Serializable)
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public boolean allocate(String type) {
        int available = inventory.getOrDefault(type, 0);
        if (available > 0) {
            inventory.put(type, available - 1);
            return true;
        }
        return false;
    }

    public void display() {
        System.out.println("\n=== Inventory ===");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "hotel_data.ser";

    // Save data
    public static void save(BookingHistory history, RoomInventory inventory) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(history);
            oos.writeObject(inventory);

            System.out.println("\nData saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load data
    public static Object[] load() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            BookingHistory history = (BookingHistory) ois.readObject();
            RoomInventory inventory = (RoomInventory) ois.readObject();

            System.out.println("Data loaded successfully.\n");

            return new Object[]{history, inventory};

        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Starting fresh.\n");
        } catch (Exception e) {
            System.out.println("Error loading data. Starting fresh.\n");
        }

        return null;
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=== Book My Stay App v12.0 ===\n");

        BookingHistory history;
        RoomInventory inventory;

        // ✅ LOAD (Recovery)
        Object[] data = PersistenceService.load();

        if (data != null) {
            history = (BookingHistory) data[0];
            inventory = (RoomInventory) data[1];
        } else {
            history = new BookingHistory();
            inventory = new RoomInventory();
        }

        // Simulate new bookings
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Suite Room"));

        System.out.println("Processing bookings...\n");

        while (!queue.isEmpty()) {
            Reservation r = queue.poll();

            if (inventory.allocate(r.getRoomType())) {
                history.add(r);
                System.out.println("Confirmed: " + r);
            } else {
                System.out.println("Failed: " + r);
            }
        }

        // Display current state
        history.display();
        inventory.display();

        // ✅ SAVE (Persistence)
        PersistenceService.save(history, inventory);

        System.out.println("\nSystem state persisted successfully.");
    }
}