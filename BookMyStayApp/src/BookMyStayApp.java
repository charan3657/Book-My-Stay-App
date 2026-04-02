import java.util.*;

// Reservation Class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// Thread-Safe Inventory
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    // ✅ synchronized critical section
    public synchronized boolean bookRoom(String roomType) {

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void display() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// Shared Booking Processor (Thread Worker)
class BookingProcessor implements Runnable {

    private Queue<Reservation> queue;
    private RoomInventory inventory;

    public BookingProcessor(Queue<Reservation> queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // ✅ synchronized queue access (critical section)
            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.poll();
            }

            // Process booking
            boolean success = inventory.bookRoom(r.getRoomType());

            if (success) {
                System.out.println(Thread.currentThread().getName() +
                        " → Booking Confirmed for " + r.getGuestName() +
                        " (" + r.getRoomType() + ")");
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " → Booking Failed for " + r.getGuestName() +
                        " (" + r.getRoomType() + ")");
            }
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=== Concurrent Booking Simulation ===\n");

        // Shared Queue
        Queue<Reservation> queue = new LinkedList<>();

        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Single Room"));
        queue.offer(new Reservation("Charlie", "Single Room"));
        queue.offer(new Reservation("David", "Suite Room"));
        queue.offer(new Reservation("Eve", "Suite Room"));

        // Shared Inventory
        RoomInventory inventory = new RoomInventory();

        // Multiple Threads (Simulating Guests)
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "Thread-2");
        Thread t3 = new Thread(new BookingProcessor(queue, inventory), "Thread-3");

        // Start Threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state
        inventory.display();

        System.out.println("\nAll bookings processed safely with thread synchronization.");
    }
}