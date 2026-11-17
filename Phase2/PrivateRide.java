public class PrivateRide extends Ride implements IPrivateRide {

    private final IRider rider;

    public PrivateRide(int rideId, String from, String to,
                       ITimeSlot timeSlot, IRider selectedRider,
                       IDriver assignedDriver) {

        super(rideId, from, to, timeSlot, assignedDriver);
        rider = selectedRider;
    }

    public IRider getRider() {
        return rider;
    }

    public boolean hasRider(int riderId) {
        return rider.getId() == riderId;
    }

    public String toString() {
        return "Private " + super.toString()
                + " | Rider " + rider.getId()
                + " " + rider.getName();
    }
}