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
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (parts.length != 4) {
                        System.err.println("Skipping invalid line " + lineNumber + ": " + line);
                        continue;
                    }
                    int year = Integer.parseInt(parts[0].trim());
                    String category = parts[1].trim();
                    List<String> laureates = parseLaureates(parts[2].trim());
                    int sharedCount = Integer.parseInt(parts[3].trim());
                    prizes.add(new NobelPrize(year, category, laureates, sharedCount));
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line);
                }
            }
        }
        System.out.println("Loaded " + prizes.size() + " entries.");
        return prizes;
    }

    private List<String> parseLaureates(String laureateStr) {
        laureateStr = laureateStr.replaceAll("^\\[|\\]$", ""); // Remove brackets
        String[] names = laureateStr.split(",\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Correctly split names while keeping quoted ones intact
        List<String> laureates = new ArrayList<>();
        for (String name : names) {
            laureates.add(name.trim().replaceAll("^['\"]|['\"]$", ""));
        }
        return laureates;
    }
}



//    private List<String> parseLaureates(String laureateStr) {
//        if (laureateStr.startsWith("[") && laureateStr.endsWith("]")) {
//            laureateStr = laureateStr.substring(1, laureateStr.length() - 1);
//        }
//        String[] names = laureateStr.split(";"); // Assuming names are separated by semicolons
//        List<String> laureates = new ArrayList<>();
//        for (String name : names) {
//            laureates.add(name.trim().replaceAll("^[\'\"]|[\'\"]$", ""));
//
//        }
//        return laureates;
//    }
//}

