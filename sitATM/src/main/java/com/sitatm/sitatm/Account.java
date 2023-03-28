package com.sitatm.sitatm;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Account {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private String accountNo;
    private String userId;
    private String pin;
    private String salt;
    private String accountType;
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean checkAccountStatus(String accountNo){
        Database db = new Database();
        boolean isDeactivated = true;
        try {
            ResultSet statusResultSet = db
                    .executeQuery("SELECT deactivation_date FROM account WHERE account_number=" + accountNo);
            if (statusResultSet.next()) {
                String deactivationDate = statusResultSet.getString("deactivation_date");
                if (deactivationDate == null) {
                    return isDeactivated = false;
                }
            }
            //db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeactivated;
    }
    public static boolean accountDeactivated(String accountNumber) {
        boolean deactivated = true;
        Database db = new Database();
        try {
            ResultSet resultSet = db
                    .executeQuery("SELECT deactivation_date FROM account WHERE account_number=" + accountNumber);
            if (resultSet.next()) {
                String deactivationDate = resultSet.getString("deactivation_date");
                if (deactivationDate == null) {
                    deactivated = false;
                }
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !deactivated;
    }

    public static boolean accountExists(String accountNumber) {
        boolean exists = false;
        Database db = new Database();
        try {
            ResultSet resultSet = db.executeQuery("SELECT * FROM account WHERE account_number=" + accountNumber);
            exists = resultSet.next();
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static void createAccount(String userId) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Account acc = new Account();
        acc.setUserId(userId);
        Database db = new Database();

        System.out.println("\nAccount Creation Process");
        int accountType;
        do {
            System.out.println("""
                Please select the Account type:
                [1] Savings Account
                [2] Current Account""");
            while (!sc.hasNextInt()) {
                System.out.println(ANSI_RED+"""
                Invalid Selection.
                Please select the Account type:
                [1] Savings Account
                [2] Current Account"""+ANSI_RESET);
                sc.next();
            }
            accountType = sc.nextInt();
        } while (accountType != 1 && accountType != 2);

        if (accountType == 1) {
            acc.setAccountType("Savings Account");
        } else {
            acc.setAccountType("Current Account");
        }
        String bankAccountNumber = null;
        while (bankAccountNumber == null) {
            // Generate 9-digit account number with prefix
            bankAccountNumber = BnkAccNumGen.generateNumber(accountType);
            // Debug
            System.out.println("Bank Account Number Generated: " + bankAccountNumber);
            try {
                ResultSet resultSet;
                String query = "SELECT account_number, user_id FROM account where account_number=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, bankAccountNumber);
                ;
                resultSet = db.executeQuery(pStatement);
                if (resultSet.next()) {
                    System.out.println(ANSI_RED+"This account number already exist!" +
                            "\nRegenerating a new account number..."+ANSI_RESET);
                    bankAccountNumber = null; // Reset Bank Account Number to null to regenerate a new account number
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
        acc.setAccountNo(bankAccountNumber);


        //System.out.println("\nCustomer to enter 6 Digit Pin: ");
        //String pin = null;
        Console console = System.console();
        System.out.println("\nCustomer to enter 6 Digit Pin: ");
        char[] pinChars = console.readPassword();
        String pin = new String(pinChars);

        while (true) {
            if (pin.matches("\\d{6}")) {
                break;
            } else {
                System.out.print(ANSI_RED+"Invalid pin. Please enter a 6 digit pin: "+ANSI_RESET);
                pinChars = console.readPassword();
                pin = new String(pinChars);
            }
        }
        // Hash customer's pin with Salt and Pepper
        String[] hashAlgo = PinHash.hashPin(pin);
        acc.setPin(hashAlgo[1]);
        acc.setSalt(hashAlgo[0]);

        // Debug
        System.out.println("Random Salt: " + hashAlgo[0]);
        System.out.println("Hashed Password: " + hashAlgo[1]);

        // Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);

        // Insert customer account into DB
        try {
            String query = "INSERT INTO account(account_number, user_id, pin, salt, account_type, created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, acc.getAccountNo());
            pStatement.setString(2, acc.getUserId());
            pStatement.setString(3, acc.getPin());
            pStatement.setString(4, acc.getSalt());
            pStatement.setString(5, acc.getAccountType());
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer account has been created successfully!");
                Email.sendEmailPrep(acc.getAccountNo(), 1);
                int choice;
                do {
                    System.out.println(ANSI_CYAN+"""
                        \nDo you want to proceed to card creation?
                        [1] Yes
                        [2] No
                        """+ANSI_RESET);
                    while (!sc.hasNextInt()) {
                        System.out.println(ANSI_RED+"""
                            \nInvalid Selection.
                            Do you want to proceed to card creation?
                            [1] Yes
                            [2] No
                            """+ANSI_RESET);
                        sc.next();
                    }
                    choice = sc.nextInt();
                } while (choice != 1 && choice != 2);

                if (choice == 1) {
                    Card.createCard(bankAccountNumber);
                } else {
                    Teller.showTellerMenu();
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class error: " + e.getMessage());
        }
    }
    public static void addAccount() {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        System.out.println("Please enter customer's current account number: ");
        String accNo = sc.next();
        String userId = null;
        try {
            ResultSet resultSet;
            resultSet = db.executeQuery("SELECT * FROM account where account_number = " + accNo);
            if (resultSet.next()) {
                userId = resultSet.getString("user_id");
                createAccount(userId);
            } else {
                int selection;
                do{
                    System.out.println(ANSI_YELLOW+"""
                        \nNo customer account found!
                        Do you want to create one now?
                        [1] Yes
                        [2] No"""+ANSI_RESET);
                    while (!sc.hasNextInt()) {
                        System.out.println(ANSI_RED+"""
                        \nInvaild Selection.
                        There is no customer account found,
                        Do you want to create one now?
                        [1] Yes
                        [2] No"""+ANSI_RESET);
                        sc.next();
                    }
                    selection = sc.nextInt();
                } while(selection != 1 && selection != 2);

                if (selection == 1) {
                    Customer.CreateNewCustomerAccount();
                } else {
                    Teller.showTellerMenu();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithmic Error: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Class Error: " + e);
        }
    }
    public static void closeAccount() throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nClose Account Process");
        System.out.println("Please enter customer's account number: ");
        String accountNumber = sc.next();
        // Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deactivationDate = sdf.format(dt);
        if (accountExists(accountNumber)) {
            if (accountDeactivated(accountNumber)) {
                Database db = new Database();
                ResultSet resultSet = db.executeQuery("SELECT * FROM account WHERE account_number=" + accountNumber);
                if (resultSet.next()) {
                    System.out.println("Getting user ID");
                    String userId = resultSet.getString("user_id");
                    int confirmation;
                    do{
                        System.out.println(ANSI_RED+"Are you sure you want to close this account?\n[1] Yes \n[2] No"+ANSI_RESET);
                        while (!sc.hasNextInt()) {
                            System.out.println(ANSI_RED+"Invalid Selection. Are you sure you want to close this account?\n[1] Yes \n[2] No"+ANSI_RESET);
                            sc.next();
                        }
                        confirmation = sc.nextInt();
                    } while(confirmation != 1 && confirmation != 2);

                    if (confirmation == 1) {
                        try {
                            String customerQuery = "UPDATE customer SET deactivation_date = ? WHERE user_id = ?";
                            String accountQuery = "UPDATE account SET deactivation_date = ? WHERE account_number = ?";
                            String cardQuery = "UPDATE card SET deactivation_date = ? WHERE account_number = ?";
                            PreparedStatement pStatement1 = db.getConnection().prepareStatement(customerQuery);
                            PreparedStatement pStatement2 = db.getConnection().prepareStatement(accountQuery);
                            PreparedStatement pStatement3 = db.getConnection().prepareStatement(cardQuery);
                            pStatement1.setString(1, deactivationDate);
                            pStatement1.setString(2, userId);
                            pStatement2.setString(1, deactivationDate);
                            pStatement2.setString(2, accountNumber);
                            pStatement3.setString(1, deactivationDate);
                            pStatement3.setString(2, accountNumber);
                            int rowsAffected1 = pStatement1.executeUpdate();
                            int rowsAffected2 = pStatement2.executeUpdate();
                            int rowsAffected3 = pStatement3.executeUpdate();
                            if (rowsAffected1 > 0 && rowsAffected2 > 0 && rowsAffected3 > 0) {
                                System.out.println("The customer's account has been deactivated.");
                            } else {
                                System.out.println("Something went wrong while processing your loan. Please try again later.");
                            }
                            db.closeConnection();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } else {
                System.out.println("Error: Account is already closed!");
            }
        } else {
            System.out.println("Error: Account does not exist!");
        }
    }
    public static void changPin() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the customer's Account Number");
        String accNo = sc.next();
        if (accountExists(accNo)) {
            if (accountDeactivated(accNo)) {
                Console console = System.console();
                System.out.println("\nCustomer to enter 6 Digit Pin: ");
                char[] pinChars = console.readPassword();
                String newPin = new String(pinChars);

                while (true) {
                    if (newPin.matches("\\d{6}")) {
                        break;
                    } else {
                        System.out.print(ANSI_RED+"Invalid pin. Please enter a 6 digit pin: "+ANSI_RESET);
                        pinChars = console.readPassword();
                        newPin = new String(pinChars);
                    }
                }

                /***
                System.out.println("Please enter the new pin number: ");
                String newPin = null;
                while (true) {
                    newPin = sc.next();
                    if (newPin.matches("\\d{6}")) {
                        break;
                    } else {
                        System.out.print("Invalid pin. Please enter a 6 digit pin: ");
                    }
                }
                 ***/
                // Hash customer's pin with Salt and Pepper
                String[] hashAlgo = PinHash.hashPin(newPin);
                String hashedPin = hashAlgo[0];
                String salt = hashAlgo[1];
                // Debug
                System.out.println("Random Salt: " + hashAlgo[0]);
                System.out.println("Hashed Password: " + hashAlgo[1]);
                try {
                    Database db = new Database();
                    String query = "UPDATE account SET pin = ?, salt = ? WHERE account_number = ?";
                    PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                    pStatement.setString(1, hashedPin);
                    pStatement.setString(2, salt);
                    pStatement.setString(3, accNo);
                    int rowsAffected = pStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Customer pin has been changed successfully!\n");
                        Teller.showTellerMenu();
                    }
                    db.closeConnection();
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Error: You cannot change pin for a deactivated account!");
            }
        } else {
            System.out.println("Error: Account does not exist!");
        }
    }
}
