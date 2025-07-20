public class Room {
    public enum Category { STANDARD, DELUXE, SUITE }
    private int roomNumber;
    private Category category;
    private double price;
    private boolean available;

    public Room(int roomNumber, Category category, double price, boolean available) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public int getRoomNumber() { return roomNumber; }
    public Category getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ", $" + price + ")";
    }
} 