package GUI;

import LOGIC.NobelPrize;
import LOGIC.CSV_READER;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NobelPrizeGUIApp extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private DetailsPanel detailsPanel;
    private StatsPanel statsPanel;
    private ChartPanel chartPanel;
    private JComboBox<String> categoryFilter;
    private List<NobelPrize> originalPrizes;

    public NobelPrizeGUIApp(List<NobelPrize> prizes) {
        this.originalPrizes = prizes;
        setTitle("Nobel Prize Winners (1950–Present)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Filter Panel
        JPanel filterPanel = new JPanel();
        categoryFilter = new JComboBox<>(getUniqueCategories(prizes));
        categoryFilter.insertItemAt("All", 0);
        categoryFilter.setSelectedIndex(0);
        categoryFilter.addActionListener(e -> applyFilter());
        filterPanel.add(new JLabel("Filter by Category:"));
        filterPanel.add(categoryFilter);
        add(filterPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Year", "Category", "Laureates", "Shared Count"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateDetailsPanel(prizes));
        add(new JScrollPane(table), BorderLayout.CENTER);
        updateTable(prizes);

        // Stats and Chart Panels
        statsPanel = new StatsPanel(prizes);
        chartPanel = new ChartPanel(createBarChart(prizes));
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.add(statsPanel);
        rightPanel.add(chartPanel);
        add(rightPanel, BorderLayout.EAST);

        // Details Panel
        detailsPanel = new DetailsPanel();
        add(detailsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void applyFilter() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        List<NobelPrize> filteredPrizes = originalPrizes.stream()
                .filter(p -> "All".equals(selectedCategory) || p.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        updateTable(filteredPrizes);
        statsPanel.updateStats(filteredPrizes);
        chartPanel.setChart(createBarChart(filteredPrizes));
    }

    private void updateTable(List<NobelPrize> prizes) {
        model.setRowCount(0);
        for (NobelPrize prize : prizes) {
            model.addRow(new Object[]{
                    prize.getYear(),
                    prize.getCategory(),
                    String.join(", ", prize.getLaureates()),
                    prize.getSharedCount()
            });
        }
    }

    private void updateDetailsPanel(List<NobelPrize> prizes) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            NobelPrize selectedPrize = prizes.get(selectedRow);
            detailsPanel.updateDetails(selectedPrize);
        }
    }

    private JFreeChart createBarChart(List<NobelPrize> prizes) {
        Map<String, Long> categoryCounts = prizes.stream()
                .collect(Collectors.groupingBy(NobelPrize::getCategory, Collectors.counting()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        categoryCounts.forEach((category, count) -> dataset.addValue(count, "Prizes", category));

        return ChartFactory.createBarChart(
                "Nobel Prizes by Category", "Category", "Number of Prizes", dataset
        );
    }

    private String[] getUniqueCategories(List<NobelPrize> prizes) {
        return prizes.stream()
                .map(NobelPrize::getCategory)
                .distinct()
                .sorted()
                .toArray(String[]::new);
    }

    public static void main(String[] args) {
        CSV_READER reader = new CSV_READER();
        try {
            List<NobelPrize> prizes = reader.read("nobel_prizes.csv");
            SwingUtilities.invokeLater(() -> new NobelPrizeGUIApp(prizes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DetailsPanel extends JPanel {
    private JLabel yearLabel, categoryLabel, laureatesLabel, sharedCountLabel;

    public DetailsPanel() {
        setLayout(new GridLayout(4, 1));
        yearLabel = new JLabel("Year: ");
        categoryLabel = new JLabel("Category: ");
        laureatesLabel = new JLabel("Laureates: ");
        sharedCountLabel = new JLabel("Shared Count: ");

        add(yearLabel);
        add(categoryLabel);
        add(laureatesLabel);
        add(sharedCountLabel);
    }

    public void updateDetails(NobelPrize prize) {
        yearLabel.setText("Year: " + prize.getYear());
        categoryLabel.setText("Category: " + prize.getCategory());
        laureatesLabel.setText("Laureates: " + String.join(", ", prize.getLaureates()));
        sharedCountLabel.setText("Shared Count: " + prize.getSharedCount());
    }
}

class StatsPanel extends JPanel {
    private JLabel totalLabel, categoryLabel, avgSharedLabel;

    public StatsPanel(List<NobelPrize> prizes) {
        setLayout(new GridLayout(3, 1));
        totalLabel = new JLabel();
        categoryLabel = new JLabel();
        avgSharedLabel = new JLabel();
        add(totalLabel);
        add(categoryLabel);
        add(avgSharedLabel);
        updateStats(prizes);
    }

    public void updateStats(List<NobelPrize> prizes) {
        totalLabel.setText("Total Prizes: " + prizes.size());
        Map<String, Long> categoryCounts = prizes.stream()
                .collect(Collectors.groupingBy(NobelPrize::getCategory, Collectors.counting()));
        String mostAwardedCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
        categoryLabel.setText("Most Awarded Category: " + mostAwardedCategory);
        double avgShared = prizes.stream().mapToInt(NobelPrize::getSharedCount).average().orElse(0);
        avgSharedLabel.setText("Average Shared Count: " + String.format("%.2f", avgShared));
    }
}
