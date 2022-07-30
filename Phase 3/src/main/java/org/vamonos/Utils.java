package main.java.org.vamonos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

//Provides common functionality to be used throughout the application.
public class Utils {
    // LinkedHashmap String -> String
    // Prompts user for input that satisfies conditions
    public static String getInput(Scanner scanner, LinkedHashMap<String,String> conditions, String prompt) {
        return getInput(scanner, conditions, prompt, false);
    }

    // LinkedHashmap String boolean -> String
    // Prompts user for input that satisfies conditions with the option of a wildcard
    public static String getInput(Scanner scanner, LinkedHashMap<String,String> conditions, String prompt, boolean allowWildcard) {
        boolean valid = false;  // Flag for if input is valid
        String input = null;

        while (!valid) {
            valid = true;   // Reset flag
            System.out.println(prompt);
            input = scanner.nextLine().strip();

            // If wildcard is allowed and used
            if(allowWildcard && input.equals("*")) {
                return "*";
            }

            // Check each condition and trip flag if condition fails
            for (String condition: conditions.keySet()) {
                if(!Pattern.matches(condition, input)) {
                    System.out.println(conditions.get(condition));
                    valid = false;
                    break;
                }
            }
        }

        return input;
    }

    // String int -> String
    // Pads string with spaces to specific size
    public static String pad(String input, int targetSize) {
        if(input.length() > targetSize) {
            return input.substring(0, targetSize);
        }
        return input + " ".repeat(targetSize - input.length());
    }

    // String int -> String
    // Pads string with zeroes on the left side to the given size
    public static String padZeroes(String input, int targetSize) {
        return "0".repeat(targetSize - input.length()) + input;
    }

    // float -> int
    // Converts a price in dollar to price in cents
    public static int priceToInt(float price) {
        return (int)(price * 100);
    }

    // int -> String
    // Converts a price in cents to a price in dollars
    public static String parsePrice(int price) {
        return (int) Math.floor(price/100) + "." + padZeroes(String.valueOf(price % 100), 2);
    }

    // String -> ArrayList<ArrayList<String>>
    // Reads a file and returns as a nested list
    public static ArrayList<ArrayList<String>> readFile(String fileName) {
        ArrayList<ArrayList<String>> contents = new ArrayList<>();  // Will hold all file contents
        File file = new File(fileName);

        if(file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Scanner scanner;
                String line;
                ArrayList<String> lineContents; // Will hold contents of one line

                while (null != (line = reader.readLine())) {
                    lineContents = new ArrayList<>();
                    scanner = new Scanner(line);

                    while (scanner.hasNext()) {
                        lineContents.add(scanner.next());
                    }

                    contents.add(lineContents); // Add current line contents to main list
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.printf("Couldn't find the file: '%s'%n", fileName);
            System.exit(1);
        }

        return contents;
    }

    // String, String, int -> null
    // Prints out the reason for ignoring a line of input
    public static void discard(String fileName, String reason, int lineNumber) {
        System.out.printf("Discarding line %d of %s because %s.%n", lineNumber + 1, fileName, reason);
    }
}
