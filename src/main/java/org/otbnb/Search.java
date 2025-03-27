package main.java.org.otbnb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static main.java.org.otbnb.RegexManager.IS_NUMBER;

//A search provides functionality related to performing a search
public class Search {

    // ArrayList<Unit> User -> Transaction
    // Manage the search transaction
    public Transaction search(Scanner scanner, ArrayList<Unit> units, User user) {
        String city = this.queryCity(scanner);
        int maxPrice = this.queryMaxPrice(scanner);
        int minBedrooms = this.queryMinBedrooms(scanner);
        // Filter list by parameters
        List<Unit> results = units.stream()
                .filter(unit -> unit.meetsCriteria(city.replaceAll("\\*", ".*"), maxPrice, minBedrooms))
                .toList();

        this.displayResult(results);

        return new Transaction(
                "04",
                user.getUsername(),
                user.getUsertype(),
                city,
                minBedrooms,
                Utils.parsePrice(maxPrice)
        );
    }

    // List<Unit> -> null
    // Display search results
    private void displayResult(List<Unit> results) {
        if (results.isEmpty()) {
            System.out.println("Sorry, no results match your criteria!");
        } else {
            System.out.println("Here are the rentals matching your search criteria:");
            System.out.println("Rental ID | Host            | City                      | Bedrooms | Price ");
            System.out.println("---------------------------------------------------------------------------");

            StringBuilder builder;
            String price;
            // For each unit in results, print the information
            for (Unit result : results) {
                price = Utils.parsePrice(result.getPrice());
                builder = new StringBuilder();
                builder.append(result.getID())
                        .append("  | ")
                        .append(Utils.pad(result.getHost(), 15))
                        .append(" | ")
                        .append(Utils.pad(result.getCity(), 25))
                        .append(" |    ")
                        .append(result.getBedrooms())
                        .append("     | ")
                        .append(" ".repeat(6 - price.length()))
                        .append(price);
                System.out.println(builder);
            }
        }
    }

    // -> String
    // Get the city from the user
    private String queryCity(Scanner scanner) {
        return Utils.getInput(scanner, new LinkedHashMap<>(), "Please enter the city name (case sensitive)...");
    }

    // -> int
    // Get the max price from the user
    public int queryMaxPrice(Scanner scanner) {
        LinkedHashMap<String, String> condition = new LinkedHashMap<>();
        condition.put(IS_NUMBER, "Sorry, that input was not understood. The input should be numeric.");

        String maxPriceInput = Utils.getInput(scanner, condition, "Please enter the maximum rental price...", true);

        int maxPrice;
        // If user inputs wildcard
        if (maxPriceInput.equals("*")) {
            return 99999;
        }
        // Cast input to integer
        maxPrice = Utils.priceToInt(Float.parseFloat(maxPriceInput));
        // If max price out of bound, return closest permissible values
        if (maxPrice > 99999) {
            return 99999;
        } else if (maxPrice < 0) {
            return 0;
        }

        return maxPrice;
    }

    // -> int
    // Get the min bedrooms from the user
    private int queryMinBedrooms(Scanner scanner) {
        LinkedHashMap<String, String> condition = new LinkedHashMap<>();
        condition.put(IS_NUMBER, "Sorry, that input was not understood. The input should be numeric.");

        String minBedroomsInput = Utils.getInput(scanner, condition, "Please enter the minimum number of bedrooms...", true);

        int minBedrooms;
        // If user inputs wildcard
        if (minBedroomsInput.equals("*")) {
            return 0;
        }
        // Cast input to integer
        minBedrooms = (int) Math.ceil(Float.parseFloat(minBedroomsInput));
        // If bedrooms out of bound, return closest permissible values
        if (minBedrooms > 9) {
            return 9;
        } else if (minBedrooms < 0) {
            return 0;
        }

        return minBedrooms;
    }
}
