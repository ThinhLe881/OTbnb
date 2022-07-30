package main.java.org.vamonos;

import java.util.ArrayList;
import java.util.regex.Pattern;

// A Unit represents a rental unit that may or may not be rented
public class Unit {
    private static int nextID = 1;
    private String host; // User hosting the unit
    private String city;
    private int price; // Represented as cents
    private int bedrooms;
    private boolean rented;
    private String id;

    // Constructors

    // For file rentals
    // Params: String, String, int, int, String, String
    public Unit(String host, String city, int price, int bedrooms, String rentalFlag, String rentalID) {
        // Get integer representation of unit rental ID
        int parsedID = Integer.parseInt(rentalID.substring(1));
        // Make sure Unit.nextID is greater than the current rental ID
        if(parsedID >= Unit.nextID){
            Unit.nextID = parsedID+1;
        }
        this.host = host;
        this.city = city;
        this.price = price;
        this.bedrooms = bedrooms;
        this.rented = rentalFlag.equals("T"); //Otherwise assume false
        this.id = rentalID;

    }

    // For new postings
    // Params: String, String, int, int
    public Unit(String host, String city, int price, int bedrooms) {
        this.host = host;
        this.city = city;
        this.price = price;
        this.bedrooms = bedrooms;
        this.rented = false;
        this.id = "A"+Utils.padZeroes(String.valueOf(Unit.nextID), 7);
        Unit.nextID++;
    }

    // ArrayList<Unit> String -> Unit or null
    // Returns the unit associated with the given unit ID (or null if there is no unit with that unit ID)
    public static Unit getUnit(ArrayList<Unit> units, String unitID){
        for(Unit unit: units){
            if(unit.id.equals(unitID)){
                return unit;
            }
        }
        return null;
    }

    // ArrayList<Unit> String -> boolean
    // Returns true if the user may rent at least one unit in the list, else false
    public static boolean anyAvailableUnits(ArrayList<Unit> units, String username){
        for(Unit unit: units){
            // If unit is available and not posted by this user
            if(!unit.rented && !username.equals(unit.host)){
                return true;
            }
        }
        return false;
    }

    // String int int -> boolean
    // Returns true if the unit satisfies all given conditions and can be rented, else false
    public boolean meetsCriteria(String regex, int price, int bedrooms){
        return !this.rented && // Unit is not rented
                this.price <= price && // Unit is not more expensive than price
                this.bedrooms >= bedrooms && // Unit has at least required amount of bedrooms
                Pattern.matches(regex, this.city); // Unit city matches input pattern
    }

    // Setter methods

    // -> null
    public void rent(){
        this.rented = true;
    }

    // Getter methods

    // -> String
    public String getHost() {
        return this.host;
    }
    // -> String
    public String getCity() {
        return this.city;
    }
    // -> int
    public int getPrice() {
        return this.price;
    }
    // -> int
    public int getBedrooms() {
        return this.bedrooms;
    }
    // -> String
    public String getID() {
        return this.id;
    }
    // -> boolean
    public boolean getRented() {
        return this.rented;
    }
}
