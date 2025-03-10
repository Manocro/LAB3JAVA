package LOGIC;

import java.util.List; // Add this line

public class NobelPrize {
    private int year;
    private String category;
    private List<String> laureates; // Requires import
    private int sharedCount;

    //Constructor to initialize a NobelPrize object with given attributes.
    public NobelPrize(int year, String category, List<String> laureates, int sharedCount) {
        this.year = year;
        this.category = category;
        this.laureates = laureates;
        this.sharedCount = sharedCount;
    }

    // Getters of each category
    public int getYear() { return year; }
    public String getCategory() { return category; }
    public List<String> getLaureates() { return laureates; }
    public int getSharedCount() { return sharedCount; }
}