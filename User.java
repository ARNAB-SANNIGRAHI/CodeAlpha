import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private double cash;
    private Portfolio portfolio;
    private List<Transaction> transactions;

    public User(String name, double cash) {
        this.name = name;
        this.cash = cash;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void buyStock(Stock stock, int quantity) {
        double total = stock.getPrice() * quantity;
        if (cash >= total) {
            cash -= total;
            portfolio.addStock(stock, quantity);
            transactions.add(new Transaction(Transaction.Type.BUY, stock, quantity, stock.getPrice(), java.time.LocalDateTime.now()));
        }
    }

    public void sellStock(Stock stock, int quantity) {
        int owned = portfolio.getHoldings().getOrDefault(stock, 0);
        if (owned >= quantity) {
            cash += stock.getPrice() * quantity;
            portfolio.removeStock(stock, quantity);
            transactions.add(new Transaction(Transaction.Type.SELL, stock, quantity, stock.getPrice(), java.time.LocalDateTime.now()));
        }
    }

    public double getPortfolioValue() {
        double value = 0;
        for (var entry : portfolio.getHoldings().entrySet()) {
            value += entry.getKey().getPrice() * entry.getValue();
        }
        return value;
    }
} 