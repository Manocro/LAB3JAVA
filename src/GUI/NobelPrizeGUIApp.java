package GUI;

import LOGIC.NobelPrize;
import LOGIC.CSV_READER;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NobelPrizeGUIApp extends JFrame {
    private JTable table;
    private DetailsPanel detailsPanel;
    private StatsPanel statsPanel;
    private ChartPanel chartPanel;

    public NobelPrizeGUIApp(List<NobelPrize> prizes) {
        setTitle("Nobel Prize Winners (1950–Present)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Table Panel
        String[] columns = {"Year", "Category", "Laureates", "Shared Count"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (NobelPrize prize : prizes) {
            model.addRow(new Object[]{
                    prize.getYear(),
                    prize.getCategory(),
                    String.join(", ", prize.getLaureates()),
                    prize.getSharedCount()
            });
        }
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateDetailsPanel(prizes));
        add(new JScrollPane(table), BorderLayout.CENTER);

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
    public StatsPanel(List<NobelPrize> prizes) {
        setLayout(new GridLayout(3, 1));
        JLabel totalLabel = new JLabel("Total Prizes: " + prizes.size());

        Map<String, Long> categoryCounts = prizes.stream()
                .collect(Collectors.groupingBy(NobelPrize::getCategory, Collectors.counting()));
        String mostAwardedCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
        JLabel categoryLabel = new JLabel("Most Awarded Category: " + mostAwardedCategory);

        double avgShared = prizes.stream().mapToInt(NobelPrize::getSharedCount).average().orElse(0);
        JLabel avgSharedLabel = new JLabel("Average Shared Count: " + String.format("%.2f", avgShared));

        add(totalLabel);
        add(categoryLabel);
        add(avgSharedLabel);
    }
}
