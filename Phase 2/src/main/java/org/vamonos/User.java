package main.java.org.vamonos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import static main.java.org.vamonos.RegexManager.*;

// User object holds the information and provides functionality to users in the system
public class User {
    private String username;
    private String usertype;
    //Flag to track if user is deleted
    private boolean deleted = false;

    // Constructor
    // Params: String, String
    public User(String userName, String userType) {
        this.username = userName;
        this.usertype =userType;
    }

    // ArrayList<User> -> String
    // Prompts user for a username, until a valid username is given, does not allow duplicates with the given list
    public static String getNewUsername(ArrayList<User> users){
        //Flag for whether user is valid
        boolean valid = false;
        String username = null;

        LinkedHashMap<String, String> conditions = new LinkedHashMap<>();
        conditions.put(NOT_EMPTY, "Sorry, usernames cannot be blank!");
        conditions.put(MAX_15_CHARS, "Sorry, usernames cannot be longer than 15 characters!");
        conditions.put(NO_SYMBOLS, "Sorry, usernames may not contain symbols!");

        String prompt = "Please enter the new username...";

        while(!valid){
            //Reset valid flag
            valid = true;
            //Get valid format username
            username = Utils.getInput(conditions, prompt);
            //Check if username already exists in user list, trip flag if so
            for(User user:users){
                if(user.username.equals(username)){
                    valid = false;
                    System.out.println("Sorry, that username has already been taken!");
                    break;
                }
            }
        }

        return username;
    }

    // -> String
    // Prompts user for a usertype, until a valid usertype is given
    public static String getNewUserType(){
        LinkedHashMap<String, String> condition = new LinkedHashMap<>();
        condition.put(VALID_USERTYPE, "Sorry, that is not a valid user type. The valid user types are 'AA', 'FS', 'PS', 'RS'.");

        String prompt = "Please enter the new user type...";

        return Utils.getInput(condition, prompt).toUpperCase();
    }

    // ArrayList<User> String -> User or null
    // Returns the requested user or null if the user does not exist
    public static User getUser(ArrayList<User> users, String userName) {
        for(User user: users){
            if(user.username.equals(userName)){
                return user;
            }
        }
        return null;
    }

    // ArrayList<User> -> User
    // Prompts user for usernames until a username in the list is given, then returns the User selected.
    public static User login(ArrayList<User> users){
        Scanner scanner = new Scanner(System.in);
        User user;

        //Until user gives good input
        while(true){
            System.out.println("Please enter a username...");
            user = getUser(users, scanner.nextLine().strip());
            if(null != user && !user.deleted){
                return user;
            }
            System.out.println("Sorry, that username doesn't exist!");
        }
    }

    // ArrayList<Unit> -> null
    // Removes every unit from the given list associated with "this" and updates the deleted flag
    public void deleteUser(ArrayList<Unit> units){
        units.removeIf(unit -> unit.getHost().equals(this.username));
        this.deleted = true;
    }

    // -> boolean
    // Returns true if "this" is admin, else false
    public boolean isAdmin(){
        return this.usertype.equals("AA");
    }

    // Getter methods

    // -> String
    public String getUsername(){
        return this.username;
    }

    // -> String
    public String getUsertype(){
        return this.usertype;
    }

    // -> boolean
    public boolean getDeleted(){
        return this.deleted;
    }


}
