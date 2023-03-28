package com.sitatm.sitatm;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Teller {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static void TellerMode() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n" +
                "Please enter your username: ");
        String username = sc.next();
        Console console = System.console();
        char[] passwordArray = console.readPassword("Enter your password: ");
        String password = String.valueOf(passwordArray);
        try {
            Database db = new Database();
            String query = "SELECT * FROM teller WHERE username=? AND password=?";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            ResultSet resultSet = db.executeQuery(pStatement);
            if (resultSet.next()) {
                String tellerName = resultSet.getString("full_name");
                System.out.println("\n" + ANSI_YELLOW +
                        "Welcome " + tellerName + "!");
                System.out.println("What would you like to do today?"+ANSI_RESET);
                showTellerMenu();
            } else {
                System.out.println(ANSI_RED+"\n" +
                        "Invalid username or password!"+ANSI_RESET);
                MainAtmCli.main(null);
            }
            db.closeConnection();
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
            String[] menuOptions = {"Create a new customer account",
                    "Add account to an existing customer",
                    "Add card to an existing customer",
                    "Close customer account",
                    "Change customer pin number",
                    "Loan Approval"};

            String leftMenuFormat = "| %-1s | %-36s |%n";

            System.out.println("+---+--------------------------------------+");
            for (int i = 1; i <= menuOptions.length; i++) {
                System.out.format(leftMenuFormat, i, menuOptions[i-1]);
                System.out.println("+---+--------------------------------------+");
            }

            System.out.format(leftMenuFormat, 0, "Quit");
            System.out.println("+---+--------------------------------------+");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> {
                        System.out.println("Option [1]: Create a new customer account");
                        Customer.CreateNewCustomerAccount();
                    }
                    case 2 -> {
                        System.out.println("Option [2]: Add account to an existing customer");
                        Account.addAccount();
                    }
                    case 3 -> {
                        System.out.println("Option [3]: Add card to an existing customer");
                        Card.addCard();
                    }

                    case 4 -> {
                        System.out.println("Option [4]: Close customer account");
                        Account.closeAccount();
                    }
                    case 5 -> {
                        System.out.println("Option [5]: Change customer's pin");
                        Account.changPin();
                    }
                    case 6 -> {
                        System.out.println("Option [6]: Loan approval");
                        Loan.loanApproval();
                    }
                    case 0 -> {
                        System.out.println("Good bye!");
                        System.exit(0);
                    }
                    default -> System.out.println(ANSI_RED+"Invalid choice, please try again."+ANSI_RESET);
                }
                if (choice >= 1 && choice <= 3) {
                    break;
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println(ANSI_RED+"Invalid input. Please enter a number."+ANSI_RESET);
                sc.nextLine();
            }
        }
    }
}
