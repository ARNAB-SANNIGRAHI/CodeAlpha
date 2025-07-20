import java.time.LocalDateTime;

public class Transaction {
    public enum Type { BUY, SELL }
    private Type type;
    private Stock stock;
    private int quantity;
    private double price;
    private LocalDateTime timestamp;

    public Transaction(Type type, Stock stock, int quantity, double price, LocalDateTime timestamp) {
        this.type = type;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 