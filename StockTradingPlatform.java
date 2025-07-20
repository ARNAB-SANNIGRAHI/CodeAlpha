import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StockTradingPlatform extends JFrame {
    private ArrayList<Stock> marketStocks;
    private User user;
    private DefaultTableModel marketTableModel;
    private DefaultTableModel portfolioTableModel;
    private JLabel cashLabel, portfolioValueLabel;
    private JComboBox<String> stockComboBox;
    private JTextField quantityField;
    private JButton buyButton, sellButton, simulateButton;

    public StockTradingPlatform() {
        setTitle("Stock Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize market stocks
        marketStocks = new ArrayList<>();
        marketStocks.add(new Stock("AAPL", "Apple Inc.", 170.00));
        marketStocks.add(new Stock("MSFT", "Microsoft Corp.", 320.00));
        marketStocks.add(new Stock("GOOGL", "Alphabet Inc.", 2800.00));
        marketStocks.add(new Stock("AMZN", "Amazon.com Inc.", 3400.00));
        marketStocks.add(new Stock("TSLA", "Tesla Inc.", 700.00));

        // Initialize user
        user = new User("Trader", 10000.00);

        // Top panel: Market data
        JPanel marketPanel = new JPanel(new BorderLayout());
        marketPanel.setBorder(BorderFactory.createTitledBorder("Market Data"));
        marketTableModel = new DefaultTableModel(new Object[]{"Symbol", "Name", "Price"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable marketTable = new JTable(marketTableModel);
        updateMarketTable();
        marketPanel.add(new JScrollPane(marketTable), BorderLayout.CENTER);
        add(marketPanel, BorderLayout.WEST);

        // Center panel: Portfolio
        JPanel portfolioPanel = new JPanel(new BorderLayout());
        portfolioPanel.setBorder(BorderFactory.createTitledBorder("Your Portfolio"));
        portfolioTableModel = new DefaultTableModel(new Object[]{"Symbol", "Name", "Quantity", "Current Price", "Total Value"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable portfolioTable = new JTable(portfolioTableModel);
        updatePortfolioTable();
        portfolioPanel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);
        add(portfolioPanel, BorderLayout.CENTER);

        // Bottom panel: Buy/Sell and Info
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel tradePanel = new JPanel();
        stockComboBox = new JComboBox<>();
        for (Stock s : marketStocks) {
            stockComboBox.addItem(s.getSymbol());
        }
        quantityField = new JTextField(5);
        buyButton = new JButton("Buy");
        sellButton = new JButton("Sell");
        simulateButton = new JButton("Simulate Price Change");
        tradePanel.add(new JLabel("Stock:"));
        tradePanel.add(stockComboBox);
        tradePanel.add(new JLabel("Quantity:"));
        tradePanel.add(quantityField);
        tradePanel.add(buyButton);
        tradePanel.add(sellButton);
        tradePanel.add(simulateButton);
        bottomPanel.add(tradePanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        cashLabel = new JLabel();
        portfolioValueLabel = new JLabel();
        infoPanel.add(cashLabel);
        infoPanel.add(portfolioValueLabel);
        bottomPanel.add(infoPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        updateInfoLabels();

        // Button actions
        buyButton.addActionListener(e -> handleTrade(true));
        sellButton.addActionListener(e -> handleTrade(false));
        simulateButton.addActionListener(e -> simulatePriceChange());
    }

    private void updateMarketTable() {
        marketTableModel.setRowCount(0);
        for (Stock s : marketStocks) {
            marketTableModel.addRow(new Object[]{s.getSymbol(), s.getName(), String.format("%.2f", s.getPrice())});
        }
    }

    private void updatePortfolioTable() {
        portfolioTableModel.setRowCount(0);
        Map<Stock, Integer> holdings = user.getPortfolio().getHoldings();
        for (Stock s : marketStocks) {
            int qty = holdings.getOrDefault(s, 0);
            if (qty > 0) {
                double totalValue = qty * s.getPrice();
                portfolioTableModel.addRow(new Object[]{s.getSymbol(), s.getName(), qty, String.format("%.2f", s.getPrice()), String.format("%.2f", totalValue)});
            }
        }
    }

    private void updateInfoLabels() {
        cashLabel.setText(String.format("Cash: $%.2f", user.getCash()));
        portfolioValueLabel.setText(String.format("  Portfolio Value: $%.2f", user.getPortfolioValue()));
    }

    private Stock getSelectedStock() {
        String symbol = (String) stockComboBox.getSelectedItem();
        for (Stock s : marketStocks) {
            if (s.getSymbol().equals(symbol)) return s;
        }
        return null;
    }

    private void handleTrade(boolean isBuy) {
        Stock stock = getSelectedStock();
        if (stock == null) return;
        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
            return;
        }
        if (isBuy) {
            double total = stock.getPrice() * qty;
            if (user.getCash() < total) {
                JOptionPane.showMessageDialog(this, "Not enough cash.");
                return;
            }
            user.buyStock(stock, qty);
        } else {
            int owned = user.getPortfolio().getHoldings().getOrDefault(stock, 0);
            if (owned < qty) {
                JOptionPane.showMessageDialog(this, "Not enough shares to sell.");
                return;
            }
            user.sellStock(stock, qty);
        }
        updatePortfolioTable();
        updateInfoLabels();
        quantityField.setText("");
    }

    private void simulatePriceChange() {
        Random rand = new Random();
        for (Stock s : marketStocks) {
            double change = (rand.nextDouble() - 0.5) * 0.1 * s.getPrice(); // +/-5%
            double newPrice = Math.max(1.0, s.getPrice() + change);
            s.setPrice(newPrice);
        }
        updateMarketTable();
        updatePortfolioTable();
        updateInfoLabels();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatform().setVisible(true));
    }
} 