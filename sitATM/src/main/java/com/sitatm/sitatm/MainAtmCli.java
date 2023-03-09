package com.sitatm.sitatm;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Calendar;


public class MainAtmCli {
    static CardGenerator CCG = new CardGenerator();
    static PinHash PH = new PinHash();
    static CVVGenerator CVVG = new CVVGenerator();
    static BnkAccNumGen BANG = new BnkAccNumGen();
    private final static int checking = 1;
    private final static int savings = 2;
    private final static String url = "jdbc:mysql://localhost:3306/sitatm";
    private final static String user = "root";
    private final static String pass = "";
    public static boolean accountExists(String accountNumber) {
        boolean exists = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM account WHERE account_number=" + accountNumber);
            exists = resultSet.next();
            connection.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    public static boolean accountDeactivated(String accountNumber){
        boolean deactivated = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT deactivation_date FROM account WHERE account_number=" + accountNumber);
            if (resultSet.next()) {
                String deactivationDate = resultSet.getString("deactivation_date");
                if(deactivationDate != null) {
                    deactivated = true;
                }
            }
            connection.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deactivated;
    }

    public static void CustomerMode() throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Your Pin");
        int pin = sc.nextInt();
        // SQL Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            // Customer table
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer WHERE pinNumber=" + pin); //
            String firstName = null;
            int checkingBalance = 0;
            int savingsBalance = 0;
            int count = 0;
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(2));
                firstName = resultSet.getString(2);
                /// totalBalance = resultSet.getInt(5);
                checkingBalance = resultSet.getInt(4);
                savingsBalance = resultSet.getInt(5);
                count++;
            }
            int choice;
            int totalBalance = checkingBalance + savingsBalance;
            int deposit_amount;
            int transfer_amount;
            int withdrawal_amount; // Not sure if we should impose a min withdraw amount
            if (count > 0) {
                System.out.println("Hello " + firstName);
                System.out.println("What would you like to do today? Please enter your choice");
                while (true) {
                    System.out.println("-----------------------------\n" +
                            "| press 1: Cash Withdraw   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 2 : Fund Transfer   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 3 : Check Balance   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 4 : All Account      |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 5 : Deposit Cash   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 6 : Other Services |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| press 7 : Exit or Quit   |\n" +
                            "-----------------------------");
                    System.out.println();

                    choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            int accType = checking;
                            boolean persist = true;
                            System.out.println("Please select the account: \nEnter 1 for Checking\nEnter 2 for Savings");
                            accType = sc.nextInt();
                            //loops until valid account type entered
                            while (persist) {
                                persist = false;
                                switch (accType) {
                                    case 1:
                                        accType = checking;
                                        break;
                                    case 2:
                                        accType = savings;
                                        break;
                                    default:
                                        System.out.print("Invalid option, please reenter");
                                        accType = sc.nextInt();
                                        persist = true;
                                }
                            }
                            System.out.println("Enter amount to withdraw");
                            withdrawal_amount = sc.nextInt();
                            if (accType == checking) {
                                while (withdrawal_amount > checkingBalance) {
                                    System.out.println("Your balance is insufficient, please re-enter amount");
                                    withdrawal_amount = sc.nextInt();
                                }
                                checkingBalance = checkingBalance - withdrawal_amount;
                                int withdrawAmountBalance = statement.executeUpdate("UPDATE customer SET aCheckingBalance = " + checkingBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have are successfully withdraw $" + withdrawal_amount + " and your current balance is $" + checkingBalance);

                            } else {
                                while (withdrawal_amount > savingsBalance) {
                                    System.out.println("Your balance is insufficient, please re-enter amount");
                                    withdrawal_amount = sc.nextInt();
                                }
                                savingsBalance = savingsBalance - withdrawal_amount;
                                int withdrawAmountBalance = statement.executeUpdate("UPDATE customer SET aSavingsBalance = " + savingsBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have are successfully withdraw $" + withdrawal_amount + " and your current balance is $" + savingsBalance);
                            }
                            break;
                        case 2:
                            System.out.println("Enter amount to transfer");
                            transfer_amount = sc.nextInt();
                            if (transfer_amount > totalBalance) {
                                System.out.println("Your balance is insufficient");
                            } else {
                                totalBalance = totalBalance - transfer_amount;
                                int transferAmountBalance = statement.executeUpdate("UPDATE customer SET aCheckingBalance = " + totalBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have are successfully withdraw $" + transfer_amount + " and your current balance is $" + totalBalance);
                            }
                            break;
                        case 3:
                            System.out.println("Your current Balance is $" + totalBalance);
                            break;
                        case 4:
                            System.out.println("Here are your list of accounts");
                            System.out.println("Your current Balance in your checking account is $" + checkingBalance);
                            System.out.println("Your current Balance in your savings account is $" + savingsBalance);
                            break;
                        case 5:
                            boolean persistent = true;
                            System.out.println("Please select the account: \nEnter 1 for Checking\nEnter 2 for Savings");
                            accType = sc.nextInt();
                            //loops until valid account type entered
                            while (persistent) {
                                persistent = false;
                                switch (accType) {
                                    case 1:
                                        accType = checking;
                                        break;
                                    case 2:
                                        accType = savings;
                                        break;
                                    default:
                                        System.out.print("Invalid option, please reenter");
                                        accType = sc.nextInt();
                                        persistent = true;
                                }
                            }
                            System.out.println("Enter amount to deposit");
                            deposit_amount = sc.nextInt();
                            if (accType == checking) {
                                checkingBalance = checkingBalance + deposit_amount;
                                int depositAmountBalance = statement.executeUpdate("UPDATE customer SET aCheckingBalance = " + checkingBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have successfully deposited $" + deposit_amount + " and your current balance is $" + checkingBalance);

                            } else {
                                savingsBalance = savingsBalance + deposit_amount;
                                int depositAmountBalance = statement.executeUpdate("UPDATE customer SET aSavingsBalance = " + savingsBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have successfully deposited $" + deposit_amount + " and your current balance is $" + savingsBalance);
                            }
                            break;
                        case 6:
                            System.out.println("Other Services");
                            System.out.println("-----------------------------\n" +
                                    "| press 1 : Change Pin Number |\n" +
                                    "-----------------------------");
                            System.out.println("-------------------------------------------\n" +
                                    "| press 2 : Update Personal Particulars   |\n" +
                                    "-------------------------------------------");
                            System.out.println("-----------------------------\n" +
                                    "| press 3 : Go back          |\n" +
                                    "-----------------------------");
                            System.out.println("-----------------------------\n" +
                                    "| press 4 : Terminate / Quit  |\n" +
                                    "-----------------------------");
                            break;
                    }
                    if (choice == 7) {
                        System.out.println("Thank you for banking with SIT ATM");
                        break;
                    }

                }
            } else {
                System.out.println("Wrong Pin !");
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL driver not found.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void TellerMode() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n" +
                "Please enter your username: ");
        String username = sc.next();
        System.out.println("\n" +
                "Please enter your password: ");
        String password = sc.next();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM teller WHERE username=? AND password=?";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            ResultSet resultSet = pStatement.executeQuery();
            if (resultSet.next()) {
                String tellerName = resultSet.getString("full_name");
                System.out.println("\n" +
                        "Welcome " + tellerName + "!");
                showTellerMenu();
            } else {
                System.out.println("\n" +
                        "Invalid username or password!");
                main(null);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL class not found. Details: \n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showTellerMenu() throws NoSuchAlgorithmException, SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("""
                What would you like to do today?
                [1] Create a new customer account
                [2] Close customer account
                [3] Change customer pin number
                [4] Exit
                """);
            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Option [1]: Create a new customer account");
                        CreateNewCustomerAccount();
                        break;
                    case 2:
                        System.out.println("Option [2]: Close customer account");
                        closeAccount();
                        break;
                    case 3:
                        System.out.println("Option [3]: Change customer's pin");
                        changPin();
                        break;
                    case 4:
                        System.out.println("Good bye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
                if (choice >= 1 && choice <= 3) {
                    break;
                }
            }catch (java.util.InputMismatchException e){
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }

    public static void CreateNewCustomerAccount() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        String NRIC = "";
        String passportNumber = "";

        System.out.println("Please enter customer's full name: ");
        String fullName = sc.nextLine();
        System.out.println("Is the customer \n[1] Singaporean/PR or \n[2] Foreigner?");
        int customerType = sc.nextInt();
        if (customerType == 1){
            System.out.println("Please enter the customer's NRIC: ");
            NRIC = sc.next();
        }else{
            System.out.println("Please enter the customer's passport number: ");
            passportNumber = sc.next();
        }
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's country: ");
        String country = sc.nextLine();
        System.out.println("Please enter the customer's gender: ");
        String gender = sc.next();
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's date of birth (YYYY-MM-DD): ");
        String dateOfBirth = sc.next();
        System.out.println("Please enter the customer's mobile number: ");
        String mobileNumber = sc.next();
        System.out.println("Please enter the customer's email: ");
        String email = sc.next();
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's address: ");
        String address = sc.nextLine();

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDateTime = sdf.format(dt);


        //Check if customer exist in the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            ResultSet resultSet;
            if (customerType == 1){
                resultSet = statement.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE nric='" + NRIC + "'");

            } else{
                resultSet = statement.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE passport_number='" + passportNumber + "'");
            }
            if (resultSet.next()) {
                System.out.println("This account already exist!");
                showTellerMenu();
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (ClassNotFoundException e){
            System.out.println("Error: MySQL driver not found.");
        }catch (SQLException e){
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }

        //Insert customer account into DB
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "INSERT INTO customer(full_name, nric, passport_number, country, gender, dob, mobile_number, address, email, created_date,deactivation_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, fullName);
            pStatement.setString(2, NRIC.toUpperCase());
            pStatement.setString(3, passportNumber.toUpperCase());
            pStatement.setString(4, country.toUpperCase());
            pStatement.setString(5, gender.toUpperCase());
            pStatement.setString(6, dateOfBirth);
            pStatement.setString(7, mobileNumber);
            pStatement.setString(8, address);
            pStatement.setString(9, email.toLowerCase());
            pStatement.setString(10, creationDateTime);
            pStatement.setNull(11,Types.DATE);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer account has been created successfully!");
                ResultSet resultSet;
                if (customerType == 1){
                    resultSet = statement.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE nric='" + NRIC + "'");
                }else{
                    resultSet = statement.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE passport_number='" + passportNumber + "'");
                }
                String userId = null;
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                    createAccount(userId);
                }
            }
            statement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createAccount(String userId) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nAccount Creation Process");
        System.out.println("""
                Please select the Account type:
                [1] Savings Account
                [2] Current Account""");
        String accountType = null;
        int selection = sc.nextInt();
        while (selection != 1 && selection != 2) {
            System.out.println("Invalid selection. Please try again.");
            selection = sc.nextInt();
        }
        if (selection == 1){
            accountType = "Savings Account";
        } else{
            accountType = "Current Account";
        }
        String bankAccountNumber = null;
        while (bankAccountNumber == null) {
            //Generate 9-digit account number with prefix
            bankAccountNumber = BANG.generateNumber(selection);
            //Debug
            System.out.println("Bank Account Number Generated: " + bankAccountNumber);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, pass);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT account_number,user_id FROM account WHERE account_number=" + bankAccountNumber);
                if (resultSet.next()) {
                    System.out.println("This account number already exist!" +
                            "\nRegenerating a new account number...");
                    bankAccountNumber = null; //Reset Bank Account Number to null to regenerate a new account number
                }
                resultSet.close();
                statement.close();
                connection.close();
            }catch (ClassNotFoundException e){
                System.out.println("Error: MySQL driver not found.");
            }catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }

        System.out.println("Customer to enter 6 Digit Pin: ");
        String pin = null;
        while (true) {
            pin = sc.next();
            if (pin.matches("\\d{6}")) {
                break;
            } else {
                System.out.print("Invalid pin. Please enter a 6 digit pin: ");
            }
        }
        //Hash customer's pin with Salt and Pepper
        String[] hashAlgo = PH.hashPin(pin);
        String hashedPin = hashAlgo[0];
        String salt = hashAlgo[1];
        //Debug
        System.out.println("Random Salt: " + hashAlgo[0]);
        System.out.println("Hashed Password: " + hashAlgo[1]);

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);

        //Insert customer account into DB
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "INSERT INTO account(account_number, user_id, pin, salt, account_type, created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, bankAccountNumber);
            pStatement.setString(2, userId);
            pStatement.setString(3, hashedPin);
            pStatement.setString(4, salt);
            pStatement.setString(5, accountType);
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer account has been created successfully!");
                //TODO: Create a card flow
                createCard(bankAccountNumber);
            }
            statement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createCard(String accNum) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nCard Creation Process");
        System.out.println("PLease select the card type:" +
                "\n[1] Debit Card" +
                "\n[2] Credit Card");
        String cardType = null;
        int selection = sc.nextInt();
        while (selection != 1 && selection != 2) {
            System.out.println("Invalid selection. Please try again.");
            selection = sc.nextInt();
        }
        if (selection == 1){
            cardType = "Debit Card";
        } else if (selection == 2) {
            cardType = "Credit Card";
        }
        //Generate Card Number
        String cardNumber = null;
        while (cardNumber == null) {
            //Generate 16 digit card number depending on Visa or Masters
            cardNumber = CCG.generateCardNumber(selection);
            //Debug
            System.out.println("ATM Card Number Generated: " + cardNumber);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, pass);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT card_number,account_number FROM card WHERE card_number=" + cardNumber);
                if (resultSet.next()) {
                    System.out.println("This card number already exist!" +
                            "\nRegenerating a new card number...");
                    cardNumber = null; //Reset cardNumber to null to regenerate a new card number
                }
                resultSet.close();
                statement.close();
                connection.close();
            }catch (ClassNotFoundException e){
                System.out.println("Error: MySQL driver not found.");
            }catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }

        //Create Expiry date (+5 years from date of issue)
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.YEAR, 5); // add 5 years
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        String formattedExpiryDate = sdf.format(expiryDate.getTime());
        //debug
        System.out.println(formattedExpiryDate);

        //generate CVV
        String cvv = CVVG.generateCVV(cardNumber, formattedExpiryDate);
        //debug
        System.out.println("CVV:"+cvv);

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "INSERT INTO card(card_number,account_number,card_type,expiry_date,cvv,created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, cardNumber);
            pStatement.setString(2, accNum);
            pStatement.setString(3, cardType);
            pStatement.setString(4, formattedExpiryDate);
            pStatement.setString(5, cvv);
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The customer's card is successfully created!");
                showTellerMenu();
            } else {
                System.out.println("Sum ting wong!");
            }
            statement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeAccount() throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nClose Account Process");
        System.out.println("Please enter customer's account number: ");
        String accountNumber = sc.next();
        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deactivationDate = sdf.format(dt);
        if (accountExists(accountNumber)){
            if(!accountDeactivated(accountNumber)){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, pass);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM account WHERE account_number=" + accountNumber);
                if (resultSet.next()) {
                    System.out.println("Getting user ID");
                    String userId = resultSet.getString("user_id");
                    System.out.println("Are you sure you want to close this account?\n[1] Yes [2] No");
                    int confirmation = sc.nextInt();
                    if (confirmation == 1) {
                        try {
                            String customerQuery = "UPDATE customer SET deactivation_date = ? WHERE user_id = ?";
                            String accountQuery = "UPDATE account SET deactivation_date = ? WHERE account_number = ?";
                            String cardQuery = "UPDATE card SET deactivation_date = ? WHERE account_number = ?";
                            PreparedStatement pStatement1 = connection.prepareStatement(customerQuery);
                            PreparedStatement pStatement2 = connection.prepareStatement(accountQuery);
                            PreparedStatement pStatement3 = connection.prepareStatement(cardQuery);
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
                                System.out.println("Sum ting wong!");
                            }
                            statement.close();
                            connection.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }else{
                System.out.println("Error: Account is already closed!");
            }
        }else {
            System.out.println("Error: Account does not exist!");
        }
    }

    public static void changPin() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the customer's Account Number");
        String accNo = sc.next();
        if (accountExists(accNo)){
            if (!accountDeactivated(accNo)){
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
                //Hash customer's pin with Salt and Pepper
                String[] hashAlgo = PH.hashPin(newPin);
                String hashedPin = hashAlgo[0];
                String salt = hashAlgo[1];
                //Debug
                System.out.println("Random Salt: " + hashAlgo[0]);
                System.out.println("Hashed Password: " + hashAlgo[1]);
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection(url, user, pass);
                    Statement statement = connection.createStatement();
                    String query = "UPDATE account SET pin = ?, salt = ? WHERE account_number = ?";
                    PreparedStatement pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, hashedPin);
                    pStatement.setString(2, salt);
                    pStatement.setString(3, accNo);
                    int rowsAffected = pStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Customer pin has been changed successfully!\n");
                        showTellerMenu();
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }else{
                System.out.println("Error: You cannot change pin for a deactivated account!");
            }
        }else{
            System.out.println("Error: Account does not exist!");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n" +
                "           ____    __    ____  _______  __        ______   ______   .___  ___.  _______ \n" +
                "           \\   \\  /  \\  /   / |   ____||  |      /      | /  __  \\  |   \\/   | |   ____|\n" +
                "            \\   \\/    \\/   /  |  |__   |  |     |  ,----'|  |  |  | |  \\  /  | |  |__   \n" +
                "             \\            /   |   __|  |  |     |  |     |  |  |  | |  |\\/|  | |   __|  \n" +
                "              \\    /\\    /    |  |____ |  `----.|  `----.|  `--'  | |  |  |  | |  |____ \n" +
                "               \\__/  \\__/     |_______||_______| \\______| \\______/  |__|  |__| |_______|\n" +
                "                                                                                        \n" +
                "    .___________.  ______           _______. __  .___________.    ___   .___________..___  ___. \n" +
                "    |           | /  __  \\         /       ||  | |           |   /   \\  |           ||   \\/   | \n" +
                "    `---|  |----`|  |  |  |       |   (----`|  | `---|  |----`  /  ^  \\ `---|  |----`|  \\  /  | \n" +
                "        |  |     |  |  |  |        \\   \\    |  |     |  |      /  /_\\  \\    |  |     |  |\\/|  | \n" +
                "        |  |     |  `--'  |    .----)   |   |  |     |  |     /  _____  \\   |  |     |  |  |  | \n" +
                "        |__|      \\______/     |_______/    |__|     |__|    /__/     \\__\\  |__|     |__|  |__| ");
        System.out.println("\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Please select the following:
                [1] Teller Mode
                [2] Customer Mode
                """);
        int mode = sc.nextInt();
        if (mode == 1) {
            TellerMode();
        } else if (mode == 2) {
            CustomerMode();
        }
    }
}
