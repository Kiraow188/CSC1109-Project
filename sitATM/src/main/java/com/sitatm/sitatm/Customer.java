package com.sitatm.sitatm;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Customer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private String userID;
    private String fName;
    private String nric;
    private String pnumber;
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{2}\\d{7}$");
    private String country;
    private String gender;
    private String dob;
    private String mNumber;
    private static final Pattern MNUMBER_PATTERN = Pattern.compile("[6|8-9]\\d{7}");
    private String address;
    private String email;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+]+@[A-Z0-9.]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);

    public Customer() {

    }
    public String getUserID() { return userID; }
    public void setUserID(String id) { this.userID = userID; }
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
            this.nric = nric;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static boolean isValidNRIC(String nric) {
        if (nric.length() != 9) {
            return false;
        }

        char firstChar = nric.charAt(0);

        if (firstChar != 'S' && firstChar != 'T' && firstChar != 'F' && firstChar != 'G') {
            return false;
        }

        int[] multipliers = {2, 7, 6, 5, 4, 3, 2};
        int total = 0;

        for (int i = 1; i < 8; i++) {
            int digit = Character.digit(nric.charAt(i), 10);
            total += digit * multipliers[i - 1];
        }

        if (firstChar == 'T' || firstChar == 'G') {
            total += 4;
        }

        int remainder = total % 11;
        char[] lastChars = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};

        if (firstChar == 'S' || firstChar == 'T') {
            return nric.charAt(8) == lastChars[remainder];
        } else {
            return nric.charAt(8) == lastChars[remainder + 4];
        }
    }
    public static boolean isValidPassport(String passportNumber) {
        return PASSPORT_PATTERN.matcher(passportNumber).matches();
    }
    public static boolean isValidMobileNumber(String mnum){
        return MNUMBER_PATTERN.matcher(mnum).matches();
    }
    public static boolean isValidEmail(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static void CreateNewCustomerAccount() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Customer cust = new Customer();
        Database db = new Database();

        System.out.println("Please enter customer's full name: ");
        String name = "";
        while (true) {
            name = sc.nextLine().trim();
            if (name.matches("^[a-zA-Z ]+$")) {
                break;
            } else {
                System.out.print(ANSI_RED + "Invalid input. Please enter only alphabets and spaces: " + ANSI_RESET);
            }
        }
        cust.setfName(name.toUpperCase());

        int customerType;
        do {
            System.out.println("\nIs the customer \n[1] Singaporean/PR or \n[2] Foreigner?");
            while (!sc.hasNextInt()) {
                System.out.println(ANSI_RED+"Invalid selection. Please enter [1] for Singaporean/PR or [2] for Foreigner:"+ANSI_RESET);
                sc.next();
            }
            customerType = sc.nextInt();
        } while (customerType != 1 && customerType != 2);

        if (customerType == 1) {
            String nric;
            boolean isValid = false;

            while (!isValid) {
                System.out.println("\nPlease enter the customer's NRIC: ");
                nric = sc.next().toUpperCase();

                if (Customer.isValidNRIC(nric)) {
                    System.out.println(nric + " is a valid NRIC number.");
                    cust.setNric(nric);
                    isValid = true;
                } else {
                    System.out.println(nric + " is not a valid NRIC number. Please enter a valid NRIC.");
                }
            }
        } else {
            String passNum;
            boolean isValid = false;
            while (!isValid) {
                System.out.println("\nPlease enter the customer's passport number: ");
                passNum = sc.next().toUpperCase();

                if (Customer.isValidPassport(passNum)) {
                    System.out.println(passNum + " is a valid passport number.");
                    cust.setPnumber(passNum);
                    isValid = true;
                } else {
                    System.out.println(passNum + " is not a valid passport number. Please enter a valid passport number");
                }
            }
        }
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's country: ");
        cust.setCountry(sc.nextLine().toUpperCase());
        System.out.println("\nPlease enter the customer's gender: ");
        cust.setGender(sc.next().toUpperCase());
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's date of birth (YYYY-MM-DD): ");
        String dobRegex = "\\d{4}-\\d{2}-\\d{2}";
        String dob = sc.next();
        while (!dob.matches(dobRegex)) {
            System.out.println(ANSI_RED+"Invalid date format. Please enter the customer's date of birth in this format YYYY-MM-DD: "+ANSI_RESET);
            dob = sc.next();
        }
        cust.setDob(dob);
        //cust.setDob(sc.next());
        // System.out.println("Please enter the customer's mobile number: ");
        // cust.setmNumber(sc.next());
        // String mNum = sc.next();
        boolean isValid = false;
        while (!isValid) {
            System.out.println("\nPlease enter the customer's mobile number: ");
            cust.setmNumber(sc.next());
            if (Customer.isValidMobileNumber(cust.getmNumber())) {
                System.out.println("Valid mobile number");
                isValid = true;
            } else {
                System.out.println("Invalid mobile number, please enter a valid mobile number.");
            }
        }
        // System.out.println("Please enter the customer's email: ");
        // cust.setEmail(sc.next());
        isValid = false;
        while (!isValid) {
            System.out.println("\nPlease enter the customer's email: ");
            cust.setEmail(sc.next());
            if (Customer.isValidEmail(cust.getEmail())) {
                System.out.println("Valid email");
                isValid = true;
            } else {
                System.out.println("Invalid email address, please enter a valid email address.");
            }
        }
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's home address: ");
        cust.setAddress(sc.nextLine());

        // Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDateTime = sdf.format(dt);

        // Check if customer exist in the database
        try {
            ResultSet resultSet;
            if (customerType == 1) {
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE nric=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getNric());
                ;
                resultSet = db.executeQuery(pStatement);

            } else {
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE passport_number=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getPnumber());
                ;
                resultSet = db.executeQuery(pStatement);
            }
            if (resultSet.next()) {
                System.out.println("This account already exist!");
                Teller.showTellerMenu();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL driver not found.");
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }

        // Insert customer account into DB
        try {
            String query = "INSERT INTO customer(full_name, nric, passport_number, country, gender, dob, mobile_number, address, email, created_date,deactivation_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, cust.getfName());
            pStatement.setString(2, cust.getNric());
            pStatement.setString(3, cust.getPnumber());
            pStatement.setString(4, cust.getCountry());
            pStatement.setString(5, cust.getGender());
            pStatement.setString(6, cust.getDob());
            pStatement.setString(7, cust.getmNumber());
            pStatement.setString(8, cust.getAddress());
            pStatement.setString(9, cust.getEmail());
            pStatement.setString(10, creationDateTime);
            pStatement.setNull(11, Types.DATE);
            if (db.executeUpdate(pStatement) > 0) {
                System.out.println("Customer account has been created successfully!");
                ResultSet resultSet;
                if (customerType == 1) {
                    System.out.println("NRIC route");
                    resultSet = db.executeQuery(
                            "SELECT user_id,nric,passport_number FROM customer WHERE nric='" + cust.getNric() + "'");
                } else {
                    System.out.println("Pnumber route");
                    resultSet = db
                            .executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE passport_number='"
                                    + cust.getPnumber() + "'");
                }
                String userId = null;
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                    Account.createAccount(userId);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }
}