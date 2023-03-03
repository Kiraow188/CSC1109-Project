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

    public static void CustomerMode() throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Your Pin");
        int pin = sc.nextInt();
        // SQL Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            java.sql.Statement statement = connection.createStatement();
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

    public static void TellerMode() throws ClassNotFoundException, SQLException {
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
            java.sql.Statement statement = connection.createStatement();
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

    public static void showTellerMenu() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("What would you like to do today?");
            System.out.println("[1] Create a new customer account");
            System.out.println("[2] Close customer account");
            System.out.println("[3] Change customer pin number");
            System.out.println("[4] Exit");
            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Create a new customer account");
                        CreateNewCustomerAccount();
                        break;
                    case 2:
                        System.out.println("Option 2 selected");
                        break;
                    case 3:
                        System.out.println("Option 3 selected");
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
        String NRIC = null;
        String passportNumber = null;

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
        System.out.println("Please enter the customer's date of birth (YYYY-MM-DD): ");
        String dateOfBirth = sc.next();
        System.out.println("Please enter the customer's mobile number: ");
        String mobileNumber = sc.next();
        System.out.println("Please enter the customer's email: ");
        String email = sc.next();
        System.out.println("Please enter the customer's address: ");
        String address = sc.nextLine();

        //Check if customer exist in the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            java.sql.Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE nric=" + NRIC + "OR passport_number=" + passportNumber);
            if (resultSet.next()) {
                System.out.println("This account already exist!");
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (ClassNotFoundException e){
            System.out.println("Error: MySQL driver not found.");
        }catch (SQLException e){
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDateTime = sdf.format(dt);

        //Insert account into DB
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            java.sql.Statement statement = connection.createStatement();
            String query = "INSERT INTO customer(full_name, nric, passport_number, country, gender, dob, mobile_number, address, email, date_created) VALUES(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, fullName);
            pStatement.setString(2, NRIC);
            pStatement.setString(3, passportNumber);
            pStatement.setString(4, country);
            pStatement.setString(5, gender);
            pStatement.setString(6, dateOfBirth);
            pStatement.setString(7, mobileNumber);
            pStatement.setString(8, address);
            pStatement.setString(9, email);
            pStatement.setString(10, creationDateTime);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer account has been created successfully!");
                //TODO: Create a card flow
                //createCard(accNum);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createCard(String cardNumber){
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
        java.text.SimpleDateFormat cd = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            java.sql.Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT user_id,account_number FROM user WHERE account_number=" + cardNumber);
            String user = null;
            if (resultSet.next()) {
                user =resultSet.getString("user_id");
            }
            String query = "INSERT INTO card(card_number,user_id,card_type,expiry_date,cvv,created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, cardNumber);
            pStatement.setString(2, user);
            pStatement.setString(3, cardType);
            pStatement.setString(4, formattedExpiryDate);
            pStatement.setString(5, cvv);
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The customer has been successfully created!");
            } else {
                System.out.println("Sum ting wong!");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        System.out.println("Please select the following: \n[1] Teller Mode \n[2] Customer Mode");
        int mode = sc.nextInt();
        if (mode == 1) {
            TellerMode();
        } else if (mode == 2) {
            CustomerMode();
        }
    }
}
