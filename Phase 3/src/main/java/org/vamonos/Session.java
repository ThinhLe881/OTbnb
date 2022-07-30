package main.java.org.vamonos;

import java.util.ArrayList;

// Each session corresponds to the user session of one user (login to logout)
// Maintains list of transactions performed and new units posted during the session
public class Session {
    private User user;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();

    // Constructor
    // Params: User
    public Session(User user){
        this.user = user;
    }

    // Transaction -> null
    // Adds the given transaction to the transaction list
    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
    }

    // Unit -> null
    // Adds the given unit to the unit list
    public void addUnit(Unit unit){
        this.units.add(unit);
    }

    // Getter methods

    // -> User
    public User getUser(){
        return this.user;
    }

    // -> ArrayList<Transaction>
    public ArrayList<Transaction> getTransactions(){
        return this.transactions;
    }

    // -> ArrayList<Transaction>
    public ArrayList<Unit> getUnits(){
        return this.units;
    }

}
