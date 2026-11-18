import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RideSharingSystem {
    // Ride indexes
    private IBST<Integer, IRide> ridesById;
    private IBST<String, IBST<Integer, IRide>> ridesByPickupLocation;
    private IBST<IDateTime, IBST<Integer, IRide>> ridesByPickupTime;

    public RideSharingSystem() {
        ridesById = new BST<Integer, IRide>();
        ridesByPickupLocation = new BST<String, IBST<Integer, IRide>>();
        ridesByPickupTime = new BST<IDateTime, IBST<Integer, IRide>>();
    }

    // Find rides from one pickup location.
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (ridesByPickupLocation.findKey(pickupLocation)) {
            IBST<Integer, IRide> innerTree = ridesByPickupLocation.retrieve();
            result = innerTree.inOrder();
        }
        return result;
    }

    // Find rides using the riders' schedules, without duplicate rides.
    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        IBST<Integer, IRide> foundRides = new BST<Integer, IRide>();
        if (ridersByName.findKey(riderName)) {
            IBST<Integer, IRider> matchingRiders = ridersByName.retrieve();
            LinkedList<IRider> ridersList = matchingRiders.inOrder();
            if (!ridersList.empty()) {
                ridersList.findFirst();
                while (true) {
                    IRider rider = ridersList.retrieve();
                    LinkedList<IRide> riderRides = rider.getSchedule().inOrder();
                    if (!riderRides.empty()) {
                        riderRides.findFirst();
                        while (true) {
                            IRide ride = riderRides.retrieve();
                            foundRides.insert(ride.getRideId(), ride);
                            if (riderRides.last()) break;
                            riderRides.findNext();
                        }
                    }
                    if (ridersList.last()) break;
                    ridersList.findNext();
                }
            }
        }
        result = foundRides.inOrder();
        return result;
    }

    // Return participants only when this is a shared ride.
    public LinkedList<IRider> getSharedRideParticipants(int rideId) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (!ridesById.findKey(rideId)) return result;
        IRide ride = ridesById.retrieve();
        if (ride instanceof ISharedRide) {
            ISharedRide sharedRide = (ISharedRide) ride;
            result = sharedRide.getParticipants();
        }
        return result;
    }

    // Locations are sorted by name, and each inner tree is sorted by ride ID.
    public LinkedList<IRide> getAllRidesAlphabetically() {
        LinkedList<IRide> result = new LinkedList<IRide>();
        LinkedList<IBST<Integer, IRide>> locations = ridesByPickupLocation.inOrder();
        if (locations.empty()) return result;
        locations.findFirst();
        while (true) {
            IBST<Integer, IRide> innerTree = locations.retrieve();
            LinkedList<IRide> locationRides = innerTree.inOrder();
            if (!locationRides.empty()) {
                locationRides.findFirst();
                while (true) {
                    result.insert(locationRides.retrieve());
                    if (locationRides.last()) break;
                    locationRides.findNext();
                }
            }
            if (locations.last()) break;
            locations.findNext();
        }
        return result;
    }

    // Get rides between the two pickup times.
    public LinkedList<IRide> searchRidesByPickupTimeRange(IDateTime from, IDateTime to) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        LinkedList<IBST<Integer, IRide>> times = ridesByPickupTime.rangeSearch(from, to);
        if (times.empty()) return result;
        times.findFirst();
        while (true) {
            IBST<Integer, IRide> innerTree = times.retrieve();
            LinkedList<IRide> timeRides = innerTree.inOrder();
            if (!timeRides.empty()) {
                timeRides.findFirst();
                while (true) {
                    result.insert(timeRides.retrieve());
                    if (timeRides.last()) break;
                    timeRides.findNext();
                }
            }
            if (times.last()) break;
            times.findNext();
        }
        return result;
    }

    // Remove the ride from schedules and all ride indexes.
    public boolean cancelRide(int rideId) {
        if (!ridesById.findKey(rideId)) return false;
        IRide ride = ridesById.retrieve();
        ITimeSlot slot = ride.getTimeSlot();
        ride.getDriver().getSchedule().removeKey(slot);
        if (ride instanceof IPrivateRide) {
            IRider rider = ((IPrivateRide) ride).getRider();
            rider.getSchedule().removeKey(slot);
        } else if (ride instanceof ISharedRide) {
            LinkedList<IRider> participants = ((ISharedRide) ride).getParticipants();
            if (!participants.empty()) {
                participants.findFirst();
                while (true) {
                    participants.retrieve().getSchedule().removeKey(slot);
                    if (participants.last()) break;
                    participants.findNext();
                }
            }
        }
        String location = ride.getPickupLocation();
        if (ridesByPickupLocation.findKey(location)) {
            IBST<Integer, IRide> innerTree = ridesByPickupLocation.retrieve();
            innerTree.removeKey(rideId);
            if (innerTree.empty()) ridesByPickupLocation.removeKey(location);
        }
        IDateTime pickupTime = slot.getStart();
        if (ridesByPickupTime.findKey(pickupTime)) {
            IBST<Integer, IRide> innerTree = ridesByPickupTime.retrieve();
            innerTree.removeKey(rideId);
            if (innerTree.empty()) ridesByPickupTime.removeKey(pickupTime);
        }
        ridesById.removeKey(rideId);
        return true;
    }

    // Remove a rider and update their rides first.
    public boolean removeRider(int riderId) {
        if (!ridersById.findKey(riderId)) return false;
        IRider rider = ridersById.retrieve();
        LinkedList<IRide> riderRides = rider.getSchedule().inOrder();
        if (!riderRides.empty()) {
            riderRides.findFirst();
            while (true) {
                IRide ride = riderRides.retrieve();
                if (ride instanceof IPrivateRide) {
                    cancelRide(ride.getRideId());
                } else if (ride instanceof ISharedRide) {
                    ISharedRide sharedRide = (ISharedRide) ride;
                    sharedRide.removeParticipantById(riderId);
                    rider.getSchedule().removeKey(ride.getTimeSlot());
                    if (sharedRide.isEmpty()) cancelRide(ride.getRideId());
                }
                if (riderRides.last()) break;
                riderRides.findNext();
            }
        }
        String name = rider.getName();
        String city = rider.getHomeCity();
        if (ridersByName.findKey(name)) {
            IBST<Integer, IRider> nameTree = ridersByName.retrieve();
            nameTree.removeKey(riderId);
            if (nameTree.empty()) ridersByName.removeKey(name);
        }
        if (ridersByHomeCity.findKey(city)) {
            IBST<Integer, IRider> cityTree = ridersByHomeCity.retrieve();
            cityTree.removeKey(riderId);
            if (cityTree.empty()) ridersByHomeCity.removeKey(city);
        }
        ridersById.removeKey(riderId);
        return true;
    }

    // Cancel the driver's rides, then remove the driver from indexes.
    public boolean removeDriver(int driverId) {
        if (!driversById.findKey(driverId)) return false;
        IDriver driver = driversById.retrieve();
        LinkedList<IRide> driverRides = driver.getSchedule().inOrder();
        if (!driverRides.empty()) {
            driverRides.findFirst();
            while (true) {
                IRide ride = driverRides.retrieve();
                cancelRide(ride.getRideId());
                if (driverRides.last()) break;
                driverRides.findNext();
            }
        }
        String plate = driver.getVehiclePlate();
        VehicleType type = driver.getVehicleType();
        if (driversByVehicleType.findKey(type)) {
            IBST<Integer, IDriver> innerTree = driversByVehicleType.retrieve();
            innerTree.removeKey(driverId);
            if (innerTree.empty()) driversByVehicleType.removeKey(type);
        }
        driversByPlate.removeKey(plate);
        driversById.removeKey(driverId);
        return true;
    }

    // Read the CSV in order and count successful rides.
    public int loadRidesFromCSV(String ridesFilePath) {
        try {
            Scanner file = new Scanner(new File(ridesFilePath));
            int count = 0;
            while (file.hasNextLine()) {
                String[] f = file.nextLine().split(",");
                try {
                    String[] pickup = f[2].split("[/ :]");
                    IDateTime pickupTime = new DateTime(
                            Integer.parseInt(pickup[2]), Integer.parseInt(pickup[0]),
                            Integer.parseInt(pickup[1]), Integer.parseInt(pickup[3]),
                            Integer.parseInt(pickup[4]));
                    String[] dropoff = f[3].split("[/ :]");
                    IDateTime dropoffTime = new DateTime(
                            Integer.parseInt(dropoff[2]), Integer.parseInt(dropoff[0]),
                            Integer.parseInt(dropoff[1]), Integer.parseInt(dropoff[3]),
                            Integer.parseInt(dropoff[4]));
                    ITimeSlot timeSlot = new TimeSlot(pickupTime, dropoffTime);
                    int driverId = Integer.parseInt(f[5].trim());
                    String[] ids = f[6].split(";");
                    int rideId = -1;
                    if (f[0].trim().equals("PRIVATE")) {
                        int riderId = Integer.parseInt(ids[0].trim());
                        rideId = schedulePrivateRide(f[1].trim(), f[4].trim(),
                                timeSlot, riderId, driverId);
                    } else if (f[0].trim().equals("SHARED")) {
                        int[] riderIds = new int[ids.length];
                        for (int i = 0; i < ids.length; i++)
                            riderIds[i] = Integer.parseInt(ids[i].trim());
                        rideId = scheduleSharedRide(f[1].trim(), f[4].trim(),
                                timeSlot, riderIds, driverId);
                    }
                    if (rideId != -1) count++;
                } catch (Exception e) {
                    // Skip this line if its data is invalid.
                }
            }
            file.close();
            return count;
        } catch (FileNotFoundException e) {
            return -1;
        }
    }
}
