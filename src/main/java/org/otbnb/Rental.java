package main.java.org.otbnb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static main.java.org.otbnb.RegexManager.*;

//A rental provides functionality related to renting an available unit
public class Rental {
    private Unit unit;
    private int numNights;

    // ArrayList<Unit> ArrayList<Unit> User -> Transaction
    // Manage the flow of rental prompt
    public Transaction book(Scanner scanner, ArrayList<Unit> units, ArrayList<Unit> sessionUnits, User user) {
        String username = user.getUsername();

        Unit unit = this.queryUnit(scanner, units, sessionUnits, username);
        this.unit = unit;

        int numNights = this.queryNumNights(scanner);
        this.numNights = numNights;

        String dailyPrice = this.printDetails();

        boolean confirm = this.confirmBooking(scanner);
        // If user cancel rental
        if (!confirm) {
            return null;
        }

        System.out.println("Rental confirmed! Enjoy your stay.");
        unit.rent(); // set rented flag to true

        return new Transaction(
                "05",
                username,
                user.getUsertype(),
                unit.getID(),
                unit.getCity(),
                unit.getBedrooms(),
                dailyPrice,
                numNights
        );
    }

    // -> int
    // Prompt the user for the number of nights
    private int queryNumNights(Scanner scanner) {
        LinkedHashMap<String, String> conditions = new LinkedHashMap<>();
        conditions.put(IS_INT, "Sorry, that input was not understood. The input should be an integer.");
        conditions.put(IS_NON_NEGATIVE_INT, "Sorry, a rental must be more than 0 night!");
        conditions.put(IS_NOT_ZERO, "Sorry, a rental must be more than 0 night!");
        conditions.put(IS_INT_UNDER_15, "Sorry, a rental can only be for at most 14 nights!");

        String numNights = Utils.getInput(scanner, conditions, "Please enter the number of nights...");
        this.numNights = Integer.parseInt(numNights);

        return this.numNights;
    }

    // ArrayList<Unit> ArrayList<Unit> String -> Unit
    // Prompt the user for the unit
    public Unit queryUnit(Scanner scanner, ArrayList<Unit> units, ArrayList<Unit> sessionUnits, String username) {
        Unit unit;
        String idToRent;

        LinkedHashMap<String, String> condition = new LinkedHashMap<>();
        condition.put(VALID_ID_FORMAT, "Sorry, that rental does not exist!");

        while(true) {
            idToRent = Utils.getInput(scanner, condition, "Please enter the rental ID...");
            if(null != Unit.getUnit(sessionUnits, idToRent)) {
                // If it is a new listing
                System.out.println("Sorry, that rental is not available for rental yet!");
            } else {
                unit = Unit.getUnit(units, idToRent);
                if (null == unit) {
                    // Unit isn't found
                    System.out.println("Sorry, that rental does not exist!");
                } else if (unit.getRented()) {
                    // Unit is already rented
                    System.out.println("Sorry, that rental is not available!");
                } else if (unit.getHost().equals(username)) {
                    // Unit belongs to this user
                    System.out.println("Sorry, you own this rental!");
                } else {
                    return unit;
                }
            }
        }
    }

    // -> String
    // Print the per night and total cost of the rental and return the daily price as a string
    private String printDetails() {
        String totalPrice = Utils.parsePrice(this.numNights * this.unit.getPrice());
        String dailyPrice = Utils.parsePrice(this.unit.getPrice());
        String suffix = this.numNights == 1 ? "" : "s"; // Add 's' if plural number of nights

        System.out.println("Confirm details:");
        System.out.printf("The rent per night is %s.\n", dailyPrice);
        System.out.printf("The total cost for %d day%s is %s.\n", this.numNights, suffix, totalPrice);

        return dailyPrice;
    }

    // -> boolean
    // Prompt the user to confirm the rental
    private boolean confirmBooking(Scanner scanner) {
        LinkedHashMap<String, String> condition = new LinkedHashMap<>();
        condition.put(YES_OR_NO, "Sorry, that input was not understood!");

        String confirmation = Utils.getInput(scanner, condition, "Please enter 'yes' to confirm your rental or 'no' to cancel.");
        if (confirmation.equalsIgnoreCase("yes")) {
            return true;
        }
        System.out.println("Rental canceled!");
        return false;
    }
}
