package LOGIC;

import java.io.*;
import java.util.*;

public class CSV_READER {
    public List<NobelPrize> read(String filename) throws IOException {
        List<NobelPrize> prizes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    // Split CSV line correctly, handling quoted values with commas
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    // Validate that the line has exactly 4 columns
                    if (parts.length != 4) {
                        System.err.println("Skipping invalid line " + lineNumber + ": " + line);
                        continue;
                    }

                    // Parse data fields
                    int year = Integer.parseInt(parts[0].trim());
                    String category = parts[1].trim();
                    List<String> laureates = parseLaureates(parts[2].trim());
                    int sharedCount = Integer.parseInt(parts[3].trim());

                    // Create a NobelPrize object and add it to the list
                    prizes.add(new NobelPrize(year, category, laureates, sharedCount));
                } catch (Exception e) {
                    // Handle parsing errors for specific lines
                    System.err.println("Error parsing line " + lineNumber + ": " + line);
                }
            }
        }
        // Print summary of loaded entries
        System.out.println("Loaded " + prizes.size() + " entries.");
        return prizes;
    }


    //Parses the laureates field from a CSV entry, handling special cases with brackets and quotes.
    private List<String> parseLaureates(String laureateStr) {
        // Remove surrounding brackets if present
        laureateStr = laureateStr.replaceAll("^\\[|\\]$", ""); // Remove brackets
        // Correctly split names while keeping quoted ones intact
        String[] names = laureateStr.split(",\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Correctly split names while keeping quoted ones intact
        List<String> laureates = new ArrayList<>();
        // Clean up names by removing extra spaces and quotes
        for (String name : names) {
            laureates.add(name.trim().replaceAll("^['\"]|['\"]$", ""));
        }
        return laureates;
    }
}