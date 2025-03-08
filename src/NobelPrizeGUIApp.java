import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.List;

public class NobelPrizeGUIApp extends JFrame {
    public NobelPrizeGUIApp(List<NobelPrize> prizes) {
        setTitle("Nobel Prize Winners (1950–Present)"); // Clear title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Column names
        String[] columns = {"Year", "Category", "Laureates", "Shared Count"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Populate table
        for (NobelPrize prize : prizes) {
            Object[] row = {
                    prize.getYear(),
                    prize.getCategory(),
                    String.join(", ", prize.getLaureates()),
                    prize.getSharedCount()
            };
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
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