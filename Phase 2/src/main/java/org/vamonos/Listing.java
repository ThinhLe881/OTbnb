package main.java.org.vamonos;

import java.util.LinkedHashMap;
import static main.java.org.vamonos.RegexManager.*;

// A listing provides functions related to listing a new unit
public class Listing {
    private String username;
    private String city;
    private int price;
    private int bedrooms;

    // Constructor
    // Params: String
    public Listing(String username){
        this.username = username;
    }

    // -> Unit
    // Prompt user for the information needed for create unit
    public Unit list() {
        this.queryCity();
        this.queryPrice();
        this.queryBedrooms();
        System.out.println("The unit has been posted!");
        return new Unit(this.username, this.city, this.price, this.bedrooms);
    }

    // -> null
    // Get the number of bedrooms from the user
    private void queryBedrooms() {
        LinkedHashMap<String, String> conditions = new LinkedHashMap<>();
        conditions.put(IS_INT, "Sorry, that input was not understood. The input should be an integer.");
        conditions.put(IS_NON_NEGATIVE_INT, "Sorry, the minimum number of bedrooms is 0!");
        conditions.put(IS_INT_UNDER_10, "Sorry, the maximum number of bedrooms is 10!");

        String numBedrooms = Utils.getInput(conditions, "Please enter the number of bedrooms...");

        this.bedrooms = Integer.parseInt(numBedrooms);
    }

    // -> null
    // Get the price from the user
    private void queryPrice() {
        LinkedHashMap<String, String> conditions = new LinkedHashMap<>();
        conditions.put(IS_NUMBER, "Sorry, that input was not understood. The input should be numeric.");
        conditions.put(IS_NON_NEGATIVE_NUMBER, "Sorry, the minimum rental price per night is 0.01!");
        conditions.put(IS_NOT_ZERO, "Sorry, the minimum rental price per night is 0.01!");
        conditions.put(MAX_2_DECIMALS, "Sorry, that input was not understood. Prices should have the maximum of two decimal places.");
        conditions.put(IS_UNDER_1000, "Sorry, the maximum rental price per night is 999.99!");

        String price = Utils.getInput(conditions, "Please enter the rental price per night...");

        this.price = Utils.priceToInt(Float.parseFloat(price));
    }

    // -> null
    // Get the city from the user
    private void queryCity() {
        LinkedHashMap<String, String> conditions = new LinkedHashMap<>();
        conditions.put(NOT_EMPTY, "Sorry, the minimum length for the city name is 1 character!");
        conditions.put(MAX_25_CHARS, "Sorry, the maximum length for the city name is 25 characters!");
        conditions.put(ONLY_LETTERS_SPACES_DASHES, "Sorry, that is not a valid city name. Only letters, spaces, and dashes are allowed");

        this.city = Utils.getInput(conditions, "Please enter the city (case-sensitive)...");
    }
}
