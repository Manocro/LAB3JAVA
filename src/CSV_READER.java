import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CSV_READER {
    public List<NobelPrize> read(String filename) throws IOException {
        List<NobelPrize> prizes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t"); // Split by tabs
                if (parts.length != 4) continue; // Skip invalid lines

                int year = Integer.parseInt(parts[0]);
                String category = parts[1];
                List<String> laureates = parseLaureates(parts[2]);
                int sharedCount = Integer.parseInt(parts[3]);

                prizes.add(new NobelPrize(year, category, laureates, sharedCount));
            }
        }
        return prizes;
    }

    private List<String> parseLaureates(String laureateStr) {
        laureateStr = laureateStr.substring(1, laureateStr.length() - 1); // Remove [ and ]
        String[] names = laureateStr.split(", ");
        List<String> laureates = new ArrayList<>();
        for (String name : names) {
            name = name.replaceAll("^['\"]|['\"]$", ""); // Remove quotes
            laureates.add(name);
        }
        return laureates;
    }
}