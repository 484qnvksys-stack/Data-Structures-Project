import java.util.Scanner;

/**
 * Command-line menu of the Ride-Sharing System (Phase 2).
 * Only this class reads input and prints output.
 */
public class Main {

    private static Scanner input = new Scanner(System.in);
    private static RideSharingSystem system = new RideSharingSystem();

    public static void main(String[] args) {
        // Load the files in this order and print how many records were loaded
        System.out.println("Riders loaded: " + system.loadRidersFromCSV("riders_100.csv"));
        System.out.println("Drivers loaded: " + system.loadDriversFromCSV("drivers_30.csv"));
        System.out.println("Rides loaded: " + system.loadRidesFromCSV("rides_40.csv"));

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    listAllRiders();
                    break;
                case 2:
                    searchRiderById();
                    break;
                case 3:
                    searchRidersByHomeCity();
                    break;
                case 4:
                    listAllRidesAlphabetically();
                    break;                
                case 5:
                    // TODO (Student 2): ask "rider or driver", then the ID; find the person with
                    // searchRiderById / searchDriverById; printList(person.getSchedule().inOrder());
                    System.out.println("Not implemented yet.");
                    break;
                case 6:
                    showIndexStatistics();
                    break;
                case 7:
                    addRider();
                    break;
                case 8:
                    searchRidesByPickupTimeRange();
                    break;
                case 9:
                    findAvailableDrivers();
                    break;
                case 10:
                    // TODO (Student 2): ask private or shared, read locations, a TimeSlot
                    // (readDateTime twice), driver ID and rider ID(s); call schedulePrivateRide /
                    // scheduleSharedRide; print the new ride ID or that it failed (-1).
                    System.out.println("Not implemented yet.");
                    break;
                case 11:
                  cancelRide();
                   break;
                case 12:
                  removeRider();
                  break;
                case 0:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Ride-Sharing System =====");
        System.out.println("1. List all riders");
        System.out.println("2. Search for a rider by ID");
        System.out.println("3. Search for riders by home city");
        System.out.println("4. List all rides alphabetically by pickup location");
        System.out.println("5. Show the schedule of a rider or a driver");
        System.out.println("6. Show index sizes and heights");
        System.out.println("7. Add a rider");
        System.out.println("8. List rides in a pickup time range");
        System.out.println("9. Find available drivers");
        System.out.println("10. Add a ride");
        System.out.println("11. Cancel a ride");
        System.out.println("12. Remove a rider");
        System.out.println("0. Exit");
    }

    // ---------------------------------------------------------- options (Jouri)

    // Option 1
    private static void listAllRiders() {
        printList(system.getAllRiders());
    }

    // Option 2
    private static void searchRiderById() {
        int id = readInt("Enter rider ID: ");
        IRider rider = system.searchRiderById(id);
        if (rider == null)
            System.out.println("No rider found with ID " + id + ".");
        else
            System.out.println(rider);
        System.out.println("Key comparisons: " + system.nbKeyCompRiderById(id));
    }

    // Option 3
    private static void searchRidersByHomeCity() {
        String city = readLine("Enter home city: ");
        printList(system.searchRidersByHomeCity(city));
    }
    
    // Option 4
    private static void listAllRidesAlphabetically() {
        printList(system.getAllRidesAlphabetically());
   }

    // Option 6
    private static void showIndexStatistics() {
        IndexName[] indexes = IndexName.values();
        System.out.printf("%-26s %6s %8s %12s%n", "Index", "Size", "Height", "Min height");
        for (int i = 0; i < indexes.length; i++) {
            int size = system.indexSize(indexes[i]);
            int height = system.indexHeight(indexes[i]);
            System.out.printf("%-26s %6d %8d %12d%n", indexes[i], size, height, minHeight(size));
        }
    }

    // Smallest possible height = ceil(log2(n + 1)): smallest h with 2^h - 1 >= n
    private static int minHeight(int n) {
        int h = 0;
        int maxNodes = 0;       // a tree of height h holds at most 2^h - 1 nodes
        while (maxNodes < n) {
            h++;
            maxNodes = maxNodes * 2 + 1;
        }
        return h;
    }

    // Option 7
    private static void addRider() {
        int id = readInt("Enter rider ID: ");
        String name = readLine("Enter full name: ");
        String email = readLine("Enter email: ");
        String phone = readLine("Enter phone number (10 digits): ");
        String city = readLine("Enter home city: ");
        try {
            IRider rider = new Rider(id, name, email, phone, city);
            if (system.addRider(rider))
                System.out.println("Rider added successfully.");
            else
                System.out.println("Failed: a rider with ID " + id + " already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
    
    // Option 8
     private static void searchRidesByPickupTimeRange() {
           IDateTime from = readDateTime("Enter start date/time (MM/DD/YYYY HH:MM): ");
           IDateTime to = readDateTime("Enter end date/time (MM/DD/YYYY HH:MM): ");

    printList(system.searchRidesByPickupTimeRange(from, to));
       }

    // Option 9
    private static void findAvailableDrivers() {
        VehicleType type = readVehicleType();
        IDateTime start = readDateTime("Enter pickup date/time (MM/DD/YYYY HH:MM): ");
        IDateTime end = readDateTime("Enter drop-off date/time (MM/DD/YYYY HH:MM): ");
        try {
            ITimeSlot slot = new TimeSlot(start, end);
            printList(system.findAvailableDrivers(slot, type));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid time slot: the pickup must be before the drop-off.");
        }
    }
    
    // Option 11
       private static void cancelRide() {
       int rideId = readInt("Enter ride ID: ");

       if (system.cancelRide(rideId))
           System.out.println("Ride cancelled successfully.");
      else
          System.out.println("Ride not found.");
     }
     
     // Option 12
         private static void removeRider() {
           int riderId = readInt("Enter rider ID: ");

             if (system.removeRider(riderId))
                System.out.println("Rider removed successfully.");
           else
             System.out.println("Rider not found.");
          }

    // ---------------------------------------------------------- input helpers

    // Prints every element of the list on its own line
    private static <T> void printList(LinkedList<T> list) {
        if (list.empty()) {
            System.out.println("No results found.");
            return;
        }
        list.findFirst();
        while (list.last() == false) {
            System.out.println(list.retrieve());
            list.findNext();
        }
        System.out.println(list.retrieve());
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    // Asks again until the user enters a whole number
    private static int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Asks again until the user enters MM/DD/YYYY HH:MM
    private static IDateTime readDateTime(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                if (text.length() != 16)
                    throw new IllegalArgumentException();
                return new DateTime(text);
            } catch (IllegalArgumentException e) {   // also catches NumberFormatException
                System.out.println("Please use the format MM/DD/YYYY HH:MM.");
            }
        }
    }

    // Asks again until the user enters a valid vehicle type
    private static VehicleType readVehicleType() {
        VehicleType[] types = VehicleType.values();
        String options = "";
        for (int i = 0; i < types.length; i++)
            options = options + types[i] + " ";
        while (true) {
            String text = readLine("Enter vehicle type (" + options.trim() + "): ");
            try {
                return VehicleType.valueOf(text.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid vehicle type.");
            }
        }
    }
}
