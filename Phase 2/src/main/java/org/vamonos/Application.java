package main.java.org.vamonos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static main.java.org.vamonos.RegexManager.*;

// Program handles the front end interface for the OT-BnB short-term rental system
// input files:
//      - current_user_accounts.txt
//      - available_rental_units.txt
// output files:
//      - daily_transaction_file.txt
//
// After being compiled, program can be run with the command "java Application"
// Once the application has been run, the user just needs to follow the prompts
// The application should be terminated with the 'quit' command when the user is logged out

public class Application {
    private ArrayList<User> users;
    private ArrayList<Unit> units;
    private Session session;

    // Constructor
    public Application() {
        this.session = null;
    }

    // Main Method
    public static void main(String[] args) {
        Application application = new Application();
        application.initialize();
        application.run();
    }

    // -> null
    // Repeatedly prompts user for a command
    private void run() {
        String command;
        Transaction transaction;
        boolean quit = false; // If user has decided to quit
        while(!quit) {
            command = this.getCommand(); // Input is validated to be a valid command
            // Unless quit states the command
            if (!command.equals("quit")) {
                System.out.println(command.substring(0,1).toUpperCase() + command.substring(1).toLowerCase() + ":");
            }
            // If user is not logged in
            if (null == this.session) {
                switch (command) {
                    case "login" -> this.session = handleLogin();
                    case "quit" -> quit = true;
                    default -> System.out.print("Sorry, you are not logged in. ");
                }
            } else {
                // If user is logged in
                transaction = null;
                switch (command) {
                    case "login" -> System.out.println("You are already logged in! Please logout to login as a different user.");
                    case "create" -> transaction = handleCreate();
                    case "delete" -> transaction = handleDelete();
                    case "post" -> transaction = handlePost();
                    case "search" -> transaction = handleSearch();
                    case "rent" -> transaction = handleRent();
                    case "logout" -> {
                        this.writeTransactions(this.session.getTransactions());
                        this.units.addAll(this.session.getUnits()); // Update application rentals with session listing
                        System.out.println("You have logged out.");
                        this.session = null;
                    }
                    case "quit" -> System.out.println("Please logout first before quitting the application.");
                }
                if (null != transaction) {
                    this.session.addTransaction(transaction);
                }
            }
        }
        // Print end-of-file code
        ArrayList<Transaction> nullTerminator = new ArrayList<>();
        nullTerminator.add(new Transaction("00"));
        writeTransactions(nullTerminator);
        System.out.println("Thank you for using OT-BnB. Goodbye!");
    }



    // -> null
    // Initialize application with the input files
    private void initialize() {
        this.users = new ArrayList<>();
        this.populateUsers("src/main/resources/current_user_accounts.txt");
        this.units = new ArrayList<>();
        this.populateUnits("src/main/resources/available_rental_units.txt");

        System.out.print("Welcome to OT-BnB! ");
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

    // ArrayList<Transaction> -> null
    // Write transactions to daily transaction file
    private void writeTransactions(ArrayList<Transaction> transactions) {
        try {
            File file = new File("output/daily_transaction_file.txt");
            PrintWriter writer;

            if(file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            }
            else {
                writer = new PrintWriter(file);
            }

            for (Transaction transaction: transactions) {
                writer.append(transaction.getOutput());
            }

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("The transactions to be written are:");

            for(Transaction transaction : transactions) {
                System.out.println(transaction.getOutput());
            }
        }
    }

    // -> String
    // Prompts user for next transaction command
    private String getCommand() {
        Scanner scanner = new Scanner(System.in);
        String input;

        List<String> commands = List.of("login", "logout", "create", "delete", "post", "search", "rent", "quit");
        String prompt;
        // Store and display the appropriate prompts based on application state
        if (null == this.session) {
            prompt = "Please login to enter transactions.";
        } else {
            prompt = String.format("Hello %s! What would you like to do?", this.session.getUser().getUsername());
        }
        System.out.println(prompt);
        // Until valid input
        while (true) {
            input = scanner.nextLine().strip(); // Get input
            for (String command : commands) {
                if (command.equalsIgnoreCase(input)) { // Check if input is valid command
                    return command;
                }
            }
            // Input is not a valid command, prompt for new command
            System.out.printf("%s:%n", input);
            if (null == this.session) {
                System.out.printf("Sorry, that command was not recognized. %s%n", prompt);
            } else {
                System.out.println("Sorry, that command was not recognized.");
                System.out.println(prompt);
            }
        }
    }

    // String -> null
    // Prints message telling user what access they do not have
    private void denyAccess(String privilege) {
        System.out.printf("Sorry, you don't have %s privilege!%n", privilege);
    }

    // -> Transaction
    // Handles a Rent transaction
    private Transaction handleRent() {
        User user = this.session.getUser();
        String userType = user.getUsertype();
        String username = user.getUsername();
        // Deny access if user cannot rent
        if (userType.equals("PS")) {
            this.denyAccess("rental");
            return null;
        }
        // Abort if there are no available rentals for this user
        if (!Unit.anyAvailableUnits(this.units, username)) {
            System.out.println("Sorry, there are no available rentals!");
            return null;
        }
        return new Rental().book(this.units, this.session.getUnits(), user);
    }

    // -> Transaction
    // Handles a Create transaction
    private Transaction handleCreate() {
        // Deny access if user isn't admin
        if (!this.session.getUser().getUsertype().equals("AA")) {
            this.denyAccess("administrator");
            return null;
        }

        String username = User.getNewUsername(this.users);
        String userType = User.getNewUserType();

        User newUser = new User(username, userType);
        this.users.add(newUser);

        System.out.printf("New user %s created!%n", username);

        return new Transaction("01", username, userType);
    }

    // -> Transaction
    // Handles a Delete transaction
    private Transaction handleDelete() {
        // Deny access if user isn't admin
        if (!this.session.getUser().getUsertype().equals("AA")) {
            this.denyAccess("administrator");
            return null;
        }
        // Get input
        String targetUsername = Utils.getInput(new LinkedHashMap<>(), "Please enter a user to be deleted...");

        // Prevent user from deleteing themself
        if (this.session.getUser().getUsername().equals(targetUsername)) {
            System.out.println("Sorry, you cannot delete yourself!");
            return null;
        }

        User targetUser = User.getUser(this.users, targetUsername);
        // Make sure user exists
        if (null == targetUser || targetUser.getDeleted()) {
            System.out.println("Sorry, that username does not exist!");
            return null;
        }
        // Make sure user is not admin
        if (targetUser.getUsertype().equals("AA")) {
            System.out.println("Sorry, admin users cannot be deleted!");
            return null;
        }
        targetUser.deleteUser(this.units); // Units passed to remove this user's units from search and rental
        System.out.printf("User %s successfully deleted!%n", targetUsername);

        return new Transaction("02", targetUsername, targetUser.getUsertype());
    }

    // -> Transaction
    // Handles a Search transaction
    private Transaction handleSearch() {
        return new Search().search(this.units, this.session.getUser());
    }

    // -> Transaction
    // Handles a Post transaction
    private Transaction handlePost() {
        User user = this.session.getUser();
        String userType = user.getUsertype();
        // Deny access if user cannot post
        if (userType.equals("RS")) {
            this.denyAccess("posting");
            return null;
        }
        // Create listing
        Listing listing = new Listing(user.getUsername());
        Unit unit = listing.list();
        // Add new unit to session units and return transaction
        this.session.addUnit(unit);
        return new Transaction(
                "03",
                user.getUsername(),
                userType,
                unit.getID(),
                unit.getCity(),
                unit.getBedrooms(),
                Utils.parsePrice(unit.getPrice())
        );
    }

    // -> Session
    // Handles a Login transaction
    private Session handleLogin() {
        return new Session(User.login(this.users));
    }
}
