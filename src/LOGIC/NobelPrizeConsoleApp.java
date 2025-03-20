package LOGIC;

import java.io.IOException;
import java.util.List;

public class NobelPrizeConsoleApp {
    public static void main(String[] args) {
        CSV_READER reader = new CSV_READER();
        try {
            List<NobelPrize> prizes = reader.read("nobel_prizes.csv");

            System.out.println("First item:");
            printPrizeDetails(prizes.get(0));

            if (prizes.size() >= 10) {
                System.out.println("\nTenth item:");
                printPrizeDetails(prizes.get(9));
            } else {
                System.out.println("\nLess than 10 entries.");
            }

            System.out.println("\nTotal entries: " + prizes.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Prints the details of the prize depending on the column.
    private static void printPrizeDetails(NobelPrize prize) {
        System.out.println("Year: " + prize.getYear());
        System.out.println("Category: " + prize.getCategory());
        System.out.println("Laureates: " + String.join(", ", prize.getLaureates()));
        System.out.println("Shared Count: " + prize.getSharedCount());
    }
}