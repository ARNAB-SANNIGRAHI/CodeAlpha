import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HotelReservationSystem extends JFrame {
    private Hotel hotel;
    private JTextField nameField, checkInField, checkOutField;
    private JButton checkInPickerBtn, checkOutPickerBtn;
    private JComboBox<String> categoryBox;
    private DefaultTableModel availableTableModel, reservationTableModel;
    private JTable availableTable, reservationTable;
    private JButton searchButton, bookButton, viewButton, cancelButton, payButton;
    private String currentUserName = "";
    private LocalDate lastCheckIn, lastCheckOut;
    private Room.Category lastCategory;

    public HotelReservationSystem() {
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        hotel = new Hotel();

        // Top panel: User info and search
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Your Name:"));
        nameField = new JTextField(10);
        topPanel.add(nameField);
        topPanel.add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{"STANDARD", "DELUXE", "SUITE"});
        topPanel.add(categoryBox);
        topPanel.add(new JLabel("Check-in (yyyy-mm-dd):"));
        checkInField = new JTextField(8);
        checkInPickerBtn = new JButton("📅");
        topPanel.add(checkInField);
        topPanel.add(checkInPickerBtn);
        topPanel.add(new JLabel("Check-out (yyyy-mm-dd):"));
        checkOutField = new JTextField(8);
        checkOutPickerBtn = new JButton("📅");
        topPanel.add(checkOutField);
        topPanel.add(checkOutPickerBtn);
        searchButton = new JButton("Search Rooms");
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Available rooms table
        availableTableModel = new DefaultTableModel(new Object[]{"Room #", "Category", "Price"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        availableTable = new JTable(availableTableModel);
        JScrollPane availableScroll = new JScrollPane(availableTable);
        availableScroll.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        availableScroll.setPreferredSize(new Dimension(350, 400));
        add(availableScroll, BorderLayout.WEST);

        // Reservations table
        reservationTableModel = new DefaultTableModel(new Object[]{"Reservation ID", "Room #", "Category", "Check-in", "Check-out", "Paid"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        JScrollPane reservationScroll = new JScrollPane(reservationTable);
        reservationScroll.setBorder(BorderFactory.createTitledBorder("Your Reservations"));
        add(reservationScroll, BorderLayout.CENTER);

        // Bottom panel: Actions
        JPanel bottomPanel = new JPanel();
        bookButton = new JButton("Book Selected Room");
        viewButton = new JButton("View Details");
        cancelButton = new JButton("Cancel Reservation");
        payButton = new JButton("Simulate Payment");
        bottomPanel.add(bookButton);
        bottomPanel.add(viewButton);
        bottomPanel.add(cancelButton);
        bottomPanel.add(payButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        searchButton.addActionListener(e -> searchRooms());
        bookButton.addActionListener(e -> bookRoom());
        viewButton.addActionListener(e -> viewDetails());
        cancelButton.addActionListener(e -> cancelReservation());
        payButton.addActionListener(e -> simulatePayment());
        checkInPickerBtn.addActionListener(e -> showDatePicker(checkInField));
        checkOutPickerBtn.addActionListener(e -> showDatePicker(checkOutField));
    }

    // Simple date picker dialog
    private void showDatePicker(JTextField targetField) {
        JDialog dialog = new JDialog(this, "Select Date", true);
        dialog.setSize(250, 150);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(this);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        SpinnerNumberModel yearModel = new SpinnerNumberModel(year, year - 5, year + 10, 1);
        SpinnerNumberModel monthModel = new SpinnerNumberModel(month + 1, 1, 12, 1);
        SpinnerNumberModel dayModel = new SpinnerNumberModel(day, 1, 31, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        JSpinner monthSpinner = new JSpinner(monthModel);
        JSpinner daySpinner = new JSpinner(dayModel);

        dialog.add(new JLabel("Year:"));
        dialog.add(yearSpinner);
        dialog.add(new JLabel("Month:"));
        dialog.add(monthSpinner);
        dialog.add(new JLabel("Day:"));
        dialog.add(daySpinner);

        JButton okBtn = new JButton("OK");
        dialog.add(okBtn);

        okBtn.addActionListener(ev -> {
            int y = (int) yearSpinner.getValue();
            int m = (int) monthSpinner.getValue();
            int d = (int) daySpinner.getValue();
            try {
                LocalDate date = LocalDate.of(y, m, d);
                targetField.setText(date.toString());
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date.");
            }
        });

        dialog.setVisible(true);
    }

    private void searchRooms() {
        String userName = nameField.getText().trim();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.");
            return;
        }
        currentUserName = userName;
        try {
            lastCategory = Room.Category.valueOf((String) categoryBox.getSelectedItem());
            lastCheckIn = LocalDate.parse(checkInField.getText().trim());
            lastCheckOut = LocalDate.parse(checkOutField.getText().trim());
            if (!lastCheckOut.isAfter(lastCheckIn)) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter valid check-in and check-out dates (yyyy-mm-dd), check-out after check-in.");
            return;
        }
        List<Room> available = hotel.searchAvailableRooms(lastCategory, lastCheckIn, lastCheckOut);
        availableTableModel.setRowCount(0);
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rooms found for the selected category and dates.");
        } else {
            for (Room r : available) {
                availableTableModel.addRow(new Object[]{r.getRoomNumber(), r.getCategory(), r.getPrice()});
            }
        }
        updateReservations();
    }

    private void bookRoom() {
        if (currentUserName.isEmpty() || lastCheckIn == null || lastCheckOut == null || lastCategory == null) {
            JOptionPane.showMessageDialog(this, "Please search for rooms first.");
            return;
        }
        int row = availableTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to book.");
            return;
        }
        int roomNum = (int) availableTableModel.getValueAt(row, 0);
        Room room = null;
        List<Room> available = hotel.searchAvailableRooms(lastCategory, lastCheckIn, lastCheckOut);
        for (Room r : available) {
            if (r.getRoomNumber() == roomNum) {
                room = r;
                break;
            }
        }
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room not available. Please refresh search.");
            searchRooms();
            return;
        }
        Reservation res = hotel.bookRoom(currentUserName, room, lastCheckIn, lastCheckOut);
        JOptionPane.showMessageDialog(this, "Room booked! Reservation ID: " + res.getReservationId());
        updateReservations();
        searchRooms();
    }

    private void updateReservations() {
        reservationTableModel.setRowCount(0);
        if (currentUserName.isEmpty()) return;
        List<Reservation> reservations = hotel.getReservationsForUser(currentUserName);
        for (Reservation res : reservations) {
            reservationTableModel.addRow(new Object[]{
                res.getReservationId(),
                res.getRoom().getRoomNumber(),
                res.getRoom().getCategory(),
                res.getCheckInDate(),
                res.getCheckOutDate(),
                res.isPaymentStatus() ? "Yes" : "No"
            });
        }
    }

    private void viewDetails() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to view details.");
            return;
        }
        String resId = (String) reservationTableModel.getValueAt(row, 0);
        Reservation res = hotel.getReservationById(resId);
        if (res != null) {
            JOptionPane.showMessageDialog(this, res.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Reservation not found.");
        }
    }

    private void cancelReservation() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to cancel.");
            return;
        }
        String resId = (String) reservationTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (hotel.cancelReservation(resId)) {
            JOptionPane.showMessageDialog(this, "Reservation cancelled.");
            updateReservations();
            searchRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Could not cancel reservation.");
        }
    }

    private void simulatePayment() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to pay for.");
            return;
        }
        String resId = (String) reservationTableModel.getValueAt(row, 0);
        Reservation res = hotel.getReservationById(resId);
        if (res != null && !res.isPaymentStatus()) {
            res.setPaymentStatus(true);
            // Remove and re-add to update persistence
            hotel.cancelReservation(resId);
            Reservation paidRes = hotel.bookRoom(res.getUserName(), res.getRoom(), res.getCheckInDate(), res.getCheckOutDate());
            paidRes.setPaymentStatus(true);
            JOptionPane.showMessageDialog(this, "Payment simulated. Reservation marked as paid.");
            updateReservations();
        } else if (res != null) {
            JOptionPane.showMessageDialog(this, "Already paid.");
        } else {
            JOptionPane.showMessageDialog(this, "Reservation not found.");
        }
    }

    public static void main(String[] args) {
        // If file errors occur, allow user to reset data
        try {
            SwingUtilities.invokeLater(() -> new HotelReservationSystem().setVisible(true));
        } catch (Exception e) {
            int reset = JOptionPane.showConfirmDialog(null, "Data files may be corrupted. Reset hotel data?", "Error", JOptionPane.YES_NO_OPTION);
            if (reset == JOptionPane.YES_OPTION) {
                java.io.File rooms = new java.io.File("rooms.dat");
                java.io.File res = new java.io.File("reservations.dat");
                rooms.delete();
                res.delete();
                SwingUtilities.invokeLater(() -> new HotelReservationSystem().setVisible(true));
            } else {
                System.exit(1);
            }
        }
    }
} 