import java.util.*;

/**
 * UseCase6RoomAllocationService
 *
 * Demonstrates booking confirmation and safe room allocation.
 * Prevents double-booking using Set and maintains inventory consistency.
 *
 * @author YourName
 * @version 6.0
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

    // Track allocated room IDs
    private Set<String> allocatedRoomIds;

    // Map room type → allocated IDs
    private Map<String, Set<String>> allocationMap;

    private int roomCounter = 1;

    public BookingService(Queue<Reservation> bookingQueue, RoomInventory inventory) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocatedRoomIds = new HashSet<>();
        this.allocationMap = new HashMap<>();
    }

    // Process all booking requests
    public void processBookings() {

        System.out.println("=== Processing Bookings ===\n");

        while (!bookingQueue.isEmpty()) {

            Reservation request = bookingQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("Processing request for " + request.getGuestName());

            // Check availability
            if (inventory.getAvailability(roomType) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(roomType);

                // Allocate room
                allocatedRoomIds.add(roomId);

                allocationMap.putIfAbsent(roomType, new HashSet<>());
                allocationMap.get(roomType).add(roomId);

                // Update inventory
                inventory.reduceAvailability(roomType);

                System.out.println("Booking Confirmed!");
                System.out.println("Guest: " + request.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Assigned Room ID: " + roomId + "\n");

            } else {
                System.out.println("Booking Failed - No availability for " + roomType + "\n");
            }
        }
    }

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        String prefix = roomType.substring(0, 2).toUpperCase();
        String roomId;

        do {
            roomId = prefix + roomCounter++;
        } while (allocatedRoomIds.contains(roomId));

        return roomId;
    }

    // Display allocations
    public void displayAllocations() {
        System.out.println("\n=== Room Allocations ===");
        for (Map.Entry<String, Set<String>> entry : allocationMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Welcome to Book My Stay App!");
        System.out.println("Hotel Booking System v6.0\n");

        // Step 1: Create booking queue (FIFO)
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Alice", "Single Room"));
        queue.offer(new Reservation("Bob", "Single Room"));
        queue.offer(new Reservation("Charlie", "Single Room")); // should fail
        queue.offer(new Reservation("David", "Suite Room"));

        // Step 2: Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Step 3: Process bookings
        BookingService bookingService = new BookingService(queue, inventory);
        bookingService.processBookings();

        // Step 4: Show results
        bookingService.displayAllocations();
        inventory.displayInventory();

        System.out.println("\nAll bookings processed safely.");
    }
}