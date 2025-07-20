import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private Map<Stock, Integer> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public void addStock(Stock stock, int quantity) {
        holdings.put(stock, holdings.getOrDefault(stock, 0) + quantity);
    }

    public void removeStock(Stock stock, int quantity) {
        int current = holdings.getOrDefault(stock, 0);
        if (current <= quantity) {
            holdings.remove(stock);
        } else {
            holdings.put(stock, current - quantity);
        }
    }

    public Map<Stock, Integer> getHoldings() {
        return holdings;
    }
} 