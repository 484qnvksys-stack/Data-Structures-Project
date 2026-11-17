public class SharedRide extends Ride implements ISharedRide {

    private  IBST<Integer, IRider> riders;

    public SharedRide(int rideId, String from, String to,
                      ITimeSlot timeSlot, IDriver assignedDriver) {

        super(rideId, from, to, timeSlot, assignedDriver);
        riders = new BST<Integer, IRider>();
    }

    public LinkedList<IRider> getParticipants() {
        return riders.inOrder();
    }

    public boolean addParticipant(IRider newRider) {

        if (hasRider(newRider.getId())) {
            return false;
        }

        return riders.insert(newRider.getId(), newRider);
    }

    public boolean removeParticipantById(int riderId) {
        return riders.removeKey(riderId);
    }

    public boolean isEmpty() {
        return riders.empty();
    }

    public boolean hasRider(int riderId) {
        return riders.findKey(riderId);
    }

    public String toString() {

        String result = "Shared " + super.toString() + " | Riders: ";

        if (riders.empty()) {
            return result + "none";
        }

        LinkedList<IRider> participants = riders.inOrder();

        participants.findFirst();

        while (!participants.last()) {

            IRider currentRider = participants.retrieve();

            result = result + currentRider.getId()
                    + " " + currentRider.getName() + ", ";

            participants.findNext();
        }

        IRider currentRider = participants.retrieve();

        return result + currentRider.getId()
                + " " + currentRider.getName();
    }
}