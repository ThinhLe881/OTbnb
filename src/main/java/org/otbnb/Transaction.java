package main.java.org.otbnb;

//A transaction records and formats all the information that needs to be printed to the daily transaction file
public class Transaction {
    private String code;
    private String username = "";
    private String usertype = "";
    private String id = "";
    private String city = "";
    private String bedrooms = "";
    private String price = "";
    private String numNights = null;

    //Constructors

    // Params: String
    // Used by quit transaction
    public Transaction(String code){
        this.code = code;
    }

    // Params: String, String, String
    // Used by create and delete transactions
    public Transaction(String code, String username, String usertype){
        this.code = code;
        this.username = username;
        this.usertype = usertype;
    }

    // Params: String, String, String, String, int, String
    // Used by search transactions
    public Transaction(String code, String username, String usertype, String city, int bedrooms, String price){
        this.code = code;
        this.username = username;
        this.usertype = usertype;
        this.city = city;
        this.bedrooms = String.valueOf(bedrooms);
        this.price = price;
    }

    // Params: String, String, String, String, String, int, String
    // Used by post transactions
    public Transaction(String code, String username, String usertype, String id, String city, int bedrooms, String price){
        this(code, username, usertype, city, bedrooms, price);
        this.id = id;
    }

    // Params: String, String, String, String, String, int, String, int
    // Used by rent transactions
    public Transaction(String code, String username, String usertype, String id, String city, int bedrooms, String price, int numNights){
        this(code, username, usertype, city, bedrooms, price);
        this.id = id;
        this.numNights = String.valueOf(numNights);
    }

    // -> String
    // Returns the string representation of the transaction formatted for the output file
    public String getOutput() {
        if(this.code.equals("00")){
            return "00\n";
        }
        String numNights = "  ";
        if (null != this.numNights) {
            numNights = Utils.padZeroes(this.numNights, 2);
        }
        return this.code + " " +
                Utils.pad(this.username, 16) +
                Utils.pad(this.usertype, 3) +
                Utils.pad(this.id, 9) +
                Utils.pad(this.city, 25) + " " +
                Utils.pad(this.bedrooms, 2) +
                (this.price.length() > 0 ? Utils.padZeroes(this.price, 6) : Utils.pad(this.price, 6)) + " " +
                numNights + '\n';
    }
}
