/**
 * A rider in the ride-sharing system.
 * Adds email and home city to the common Person fields.
 * Riders are compared by ID.
 * Phase 2: no setters for email and city, because they are BST keys and must not change.
 */
public class Rider extends Person implements IRider {

    private final String email;
    private final String homeCity;

    public Rider(int id, String name, String email, String phoneNumber, String homeCity) {
        super(id, name, phoneNumber);   // Person checks the phone number
        this.email = email;
        this.homeCity = homeCity;
    }

    public String getEmail() {
        return email;
    }

    public String getHomeCity() {
        return homeCity;
    }

    // Negative, zero or positive as this ID is smaller, equal or larger
    public int compareTo(IRider other) {
        return Integer.compare(getId(), other.getId());
    }

    public String toString() {
        return "Rider " + super.toString() + " | " + email + " | " + homeCity;
    }
}
