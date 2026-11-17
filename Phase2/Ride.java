public abstract class Ride implements IRide {

    private final int rideId;
    private final String startLocation;
    private final String endLocation;
    private final ITimeSlot timeSlot;
    private final IDriver assignedDriver;

    public Ride(int rideId, String from, String to,
                ITimeSlot timeSlot, IDriver driverInfo) {

        this.rideId = rideId;
        startLocation = from;
        endLocation = to;
        this.timeSlot = timeSlot;
        assignedDriver = driverInfo;
    }

    public int getRideId() {
        return rideId;
    }

    public String getPickupLocation() {
        return startLocation;
    }

    public String getDropoffLocation() {
        return endLocation;
    }

    public ITimeSlot getTimeSlot() {
        return timeSlot;
    }

    public IDriver getDriver() {
        return assignedDriver;
    }

    public int compareTo(IRide other) {
        return Integer.compare(rideId, other.getRideId());
    }

    public abstract boolean hasRider(int riderId);

    public String toString() {
        return "Ride " + rideId
                + " | " + startLocation + " " + timeSlot.getStart().format()
                + " -> " + endLocation + " " + timeSlot.getEnd().format()
                + " | Driver " + assignedDriver.getId()
                + " " + assignedDriver.getName();
    }
}