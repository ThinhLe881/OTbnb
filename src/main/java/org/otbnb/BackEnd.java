package main.java.org.otbnb;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static main.java.org.otbnb.RegexManager.*;
import static main.java.org.otbnb.RegexManager.VALID_USERTYPE;

public class BackEnd {
    private ArrayList<User> users;
    private ArrayList<Unit> units;

    String daily_transaction_file;
    String current_accounts_output;
    String available_rentals_output;

    // Main Method
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.printf("Expected 5 arguments, but received %d.\n", args.length);
            System.exit(1);
        }
        BackEnd backEnd = new BackEnd();
        backEnd.initialize(args);
        backEnd.process();
        backEnd.writeCurrentAccounts();
        backEnd.writeAvailableRentals();
    }


    // -> null
    // Write current users from backend to new current_user_accounts file
    private void writeCurrentAccounts() {
        try {
            File file = new File(this.current_accounts_output);

            PrintWriter writer;

            if(file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            }
            else {
                try {
                    writer = new PrintWriter(file);
                } catch (FileNotFoundException e) {
                    writer = new PrintWriter("temp_current_accounts_file.txt");
                }
            }

            for (User user: this.users) {
                writer.append(user.getUserAsString());
                writer.append(System.getProperty("line.separator"));
            }

            writer.append("END" + System.getProperty("line.separator"));

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("The users to be written are:");

            for(User user : this.users) {
                System.out.println(user.getUserAsString());
            }
        }
    }


    // -> null
    // Write current units from backend to new available_rental_units file
    private void writeAvailableRentals() {
        try {
            File file = new File(this.available_rentals_output);

            PrintWriter writer;

            if(file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            }
            else {
                try {
                    writer = new PrintWriter(file);
                } catch (FileNotFoundException e) {
                    writer = new PrintWriter("temp_available_rentals_file.txt");
                }
            }

            for (Unit unit: this.units) {
                writer.append(unit.getUnitAsString());
                writer.append(System.getProperty("line.separator"));
            }

            writer.append("END" + System.getProperty("line.separator"));

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("The units to be written are:");

            for(Unit unit : this.units) {
                System.out.println(unit.getUnitAsString());
            }
        }
    }


    // -> null
    // Process current users and units based on daily_transaction_file file
    private void process() {
        ArrayList<ArrayList<String>> contents = Utils.readFile(this.daily_transaction_file);
        ArrayList<String> line;

        /*
        Arguments for each line
          0 -> transaction code (00 - EOF, 01 - create, 02 - delete, 03 - list, 04 - search, 05 - rent)
          1 -> user (or nothing if 00)
          2 -> userType (or nothing if 00)
          3 -> unitId (or nothing if 00, 01, 02)
          4 -> city name (or nothing if 00, 01, 02)
          5 -> number of bedrooms (or nothing if 00, 01, 02)
          6 -> price (or nothing if 00, 01, 02)
          7 -> number of days remaining (if 05)
        */

        for (int i = 0; i < contents.size(); i++) {
            line = contents.get(i); // The line being processed

            switch (line.get(0)) {
                case "00", "04" -> {}
                case "01" -> processCreate(line);
                case "02" -> processDelete(line);
                case "03" -> processListing(line);
                case "05" -> processRent(line);
            }
        }

        for (Unit unit : this.units) {
            unit.updateDaysRemaining();
        }
    }


    // ArrayList<String> -> null
    // Set rent status for new rentals
    private void processRent(ArrayList<String> line) {
        Unit unit = Unit.getUnit(this.units, line.get(3));
        unit.rent();
        unit.setDaysRemaining(Integer.parseInt(line.get(7)));

    }


    // ArrayList<String> -> null
    // Remove deleted users from backed
    private void processDelete(ArrayList<String> line) {
        User user = User.getUser(this.users, line.get(1));

        user.deleteUser(this.units);
        this.users.remove(user);
    }


    // ArrayList<String> -> null
    // Add new users to backend
    private void processCreate(ArrayList<String> line) {
        User user = new User(line.get(1), line.get(2));

        this.users.add(user);
    }


    // ArrayList<String> -> null
    // Add new listings to backend
    private void processListing(ArrayList<String> line) {
        Unit unit = new Unit(
                line.get(1),
                line.get(4),
                Utils.priceToInt(Float.parseFloat(line.get(6))),
                Integer.parseInt(line.get(5)),
                "F",
                line.get(3));

        this.units.add(unit);
    }


    // String[] -> null
    // Initialize application with the input files
    private void initialize(String[] args) {
        this.users = new ArrayList<>();
        this.units = new ArrayList<>();

        String current_accounts_input = args[0];
        String available_rentals_input = args[1];
        this.daily_transaction_file = args[2];

        this.current_accounts_output = args[3];
        this.available_rentals_output = args[4];

        this.populateUsers(current_accounts_input);
        this.populateUnits(available_rentals_input);
    }


    // String -> null
    // Populate rental units from file
    private void populateUnits(String fileName) {
        ArrayList<ArrayList<String>> contents = Utils.readFile(fileName);
        String fileDescription = "the available rentals file";
        ArrayList<String> line;
        String reason;
        Unit unit;

        /*
          Arguments for each line
          0 -> rentalID
          1 -> userName
          2 -> city
          3 -> bedrooms
          4 -> price
          5 -> rentalFlag
          6 -> number of days left if rented
         */
        for (int i = 0; i < contents.size(); i++) {
            line = contents.get(i); // The line being processed
            reason = null;

            if (line.size() < 6) {
                reason = "is missing contents";
            }
            else if (line.size() > 7) {
                reason = "contains many arguments";
            }
            else if (!Pattern.matches(VALID_ID_FORMAT, line.get(0))) {
                reason = "rental id is not valid";
            }
            else if (line.get(1).length() > 15) {
                reason = "user name is too long";
            }
            else if (line.get(2).length() > 25) {
                reason = "city name is too long";
            }
            else if (!Pattern.matches(IS_NON_NEGATIVE_INT, line.get(3))) {
                reason = "the number of bedrooms is not a non negative integer";
            }
            else if (Integer.parseInt(line.get(3)) > 9) {
                reason = "the number of bedrooms is too large";
            }
            else if (!Pattern.matches(IS_NON_NEGATIVE_NUMBER, line.get(4))) {
                reason = "the price is not a non negative number";
            }
            else if (Float.parseFloat(line.get(4)) > 999.99) {
                reason = "the price is too high";
            }

            // If nothing went wrong
            if (null == reason) {
                unit = new Unit(line.get(1),
                        line.get(2),
                        Utils.priceToInt(Float.parseFloat(line.get(4))),
                        Integer.parseInt(line.get(3)),
                        line.get(5),
                        line.get(0));

                // Added to backend
                if (unit.getRented()) {
                    unit.setDaysRemaining(Integer.parseInt(line.get(6)));
                }

                this.units.add(unit);
            }

            // Print error message unless file is done
            else {
                if (line.isEmpty() || !line.get(0).equals("END")) {
                    Utils.discard(fileDescription, reason, i);
                }
            }
        }
    }


    // String -> null
    // Populate users from file
    private void populateUsers(String fileName) {
        ArrayList<ArrayList<String>> contents = Utils.readFile(fileName);
        String fileDescription = "the user accounts file";
        ArrayList<String> line;
        String reason;
        User user;

        for (int i = 0; i < contents.size(); i++) {
            line = contents.get(i); // The line being processed
            reason = null;

            if (line.size() < 2) {
                reason = "is missing contents";
            }
            else if (line.size() > 2) {
                reason = "contains many arguments";
            }
            else if (line.get(0).length() > 15) {
                reason = "username is too long";
            }
            else if (!Pattern.matches(VALID_USERTYPE, line.get(1))) {
                reason = "user type is not valid";
            }
            else if (null != User.getUser(this.users, line.get(0))) {
                reason = "there is a duplicate user";
            }

            // If nothing went wrong
            if (null == reason) {
                user = new User(line.get(0), line.get(1));
                this.users.add(user);
            }
            // Print error message unless file is done
            else {
                if (line.isEmpty() || !line.get(0).equals("END")) {
                    Utils.discard(fileDescription, reason, i);
                }
            }
        }

    }
}
