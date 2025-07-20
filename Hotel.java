import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;
    private static final String ROOMS_FILE = "rooms.dat";
    private static final String RESERVATIONS_FILE = "reservations.dat";

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        loadRooms();
        loadReservations();
    }

    // Initialize default rooms if file not found
    private void loadRooms() {
        File file = new File(ROOMS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                rooms = (List<Room>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                initDefaultRooms();
            }
        } else {
            initDefaultRooms();
        }
    }

    private void initDefaultRooms() {
        rooms.clear();
        int roomNum = 1;
        for (int i = 0; i < 5; i++) rooms.add(new Room(roomNum++, Room.Category.STANDARD, 100, true));
        for (int i = 0; i < 3; i++) rooms.add(new Room(roomNum++, Room.Category.DELUXE, 200, true));
        for (int i = 0; i < 2; i++) rooms.add(new Room(roomNum++, Room.Category.SUITE, 400, true));
        saveRooms();
    }

    private void saveRooms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                reservations = (List<Reservation>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                reservations = new ArrayList<>();
            }
        } else {
            reservations = new ArrayList<>();
        }
    }

    private void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Room> searchAvailableRooms(Room.Category category, LocalDate checkIn, LocalDate checkOut) {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCategory() == category && isRoomAvailable(room, checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation res : reservations) {
            if (res.getRoom().getRoomNumber() == room.getRoomNumber()) {
                // Overlapping dates
                if (!(checkOut.isBefore(res.getCheckInDate()) || checkIn.isAfter(res.getCheckOutDate().minusDays(1)))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Reservation bookRoom(String userName, Room room, LocalDate checkIn, LocalDate checkOut) {
        String reservationId = UUID.randomUUID().toString().substring(0, 8);
        Reservation res = new Reservation(reservationId, userName, room, checkIn, checkOut, false);
        reservations.add(res);
        saveReservations();
        return res;
    }

    public boolean cancelReservation(String reservationId) {
        Iterator<Reservation> it = reservations.iterator();
        while (it.hasNext()) {
            Reservation res = it.next();
            if (res.getReservationId().equals(reservationId)) {
                it.remove();
                saveReservations();
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getReservationsForUser(String userName) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getUserName().equalsIgnoreCase(userName)) {
                result.add(res);
            }
        }
        return result;
    }

    public Reservation getReservationById(String reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId().equals(reservationId)) {
                return res;
            }
        }
        return null;
    }
} 