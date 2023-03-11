package com.sitatm.sitatm;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Calendar;


public class MainAtmCli {
    private final static int checking = 1;
    private final static int savings = 2;
    private final static String url = "jdbc:mysql://localhost:3306/sitatm";
    private final static String user = "root";
    private final static String pass = "";

    // Color Codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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
        System.out.println("\nPlease Enter Your userID");
        String userId = sc.next();
        System.out.println("Please Enter Your Pin");
        int pin = sc.nextInt();
        System.out.println();

        // SQL Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            // Customer table
            String SAaccNo = null;
            String CAaccNo = null;
            String pinhash = null;
            String salt = null;
            ResultSet SAloginSet = statement
                    .executeQuery("SELECT * FROM `account` WHERE `user_id` =" + userId + " AND `account_type` = 'Savings Account'"); // Need to check Pin 
            while (SAloginSet.next()) {
                SAaccNo = SAloginSet.getString(1);
                pinhash = SAloginSet.getString(3);
                salt = SAloginSet.getString(4);
            }
            ResultSet CAloginSet = statement
                    .executeQuery("SELECT * FROM `account` WHERE `user_id` =" + userId + " AND `account_type` = 'Current Account'"); //  Need to check Pin
            while (CAloginSet.next()) {
                CAaccNo = CAloginSet.getString(1);
                pinhash = CAloginSet.getString(3);
                salt = CAloginSet.getString(4);
            } 

            String firstName = null;
            double checkingBalance = 0;
            double savingsBalance = 0;
            int count = 0;
            ResultSet SAresultSet = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.user_id = "
                                    + userId + " AND account.account_number = " + SAaccNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
            while (SAresultSet.next()) {
                // for (int i = 0; i < resultSet.getFetchSize(); i++) {
                // System.out.println(resultSet.getString(i));
                // }
                // System.out.println(resultSet.getString(2));
                firstName = SAresultSet.getString(8);
                /// totalBalance = resultSet.getInt(5);
                savingsBalance = SAresultSet.getInt(25);
                count++;
            }
            ResultSet CAresultSet = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.user_id = "
                                    + userId + " AND account.account_number = " + CAaccNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
            while (CAresultSet.next()) {
                // for (int i = 0; i < resultSet.getFetchSize(); i++) {
                // System.out.println(resultSet.getString(i));
                // }
                // System.out.println(resultSet.getString(2));
                firstName = CAresultSet.getString(8);
                /// totalBalance = resultSet.getInt(5);
                checkingBalance = CAresultSet.getInt(25);
                count++;
            }
            int choice;
            double totalBalance = checkingBalance + savingsBalance;
            double deposit_amount;
            double transfer_amount;
            double withdrawal_amount; // Not sure if we should impose a min withdraw amount
            if (count > 0) {
                System.out.println(ANSI_CYAN + "Hello " + firstName + ",");
                System.out.println("As of " + new java.util.Date() + "," + ANSI_RESET);
                System.out.println("  -----------------------------");
                System.out.printf("  | Saving Balance: $%.2f    |\n", savingsBalance);
                System.out.println("  -----------------------------");
                System.out.printf("  | Checking Balance: $%.2f |\n", checkingBalance);
                System.out.println("  -----------------------------");
                System.out.printf("  | Total Balance: $%.2f    |\n", totalBalance);
                System.out.println("  -----------------------------\n");
                System.out.println(
                        ANSI_YELLOW + "What would you like to do today? Please enter your choice" + ANSI_RESET);
                while (true) {
                    System.out.println("-----------------------------\n" +
                            "| [press 1]: Cash Withdraw   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 2] : Fund Transfer  |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 3] : Check Balance  |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 4] : All Account    |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 5] : Deposit Cash   |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 6] : Other Services |\n" +
                            "-----------------------------");
                    System.out.println("-----------------------------\n" +
                            "| [press 7] : Exit or Quit   |\n" +
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
                                        System.out.print("Invalid option, please re-enter");
                                        accType = sc.nextInt();
                                        persist = true;
                                }
                            }
                            System.out.println("Enter amount to withdraw: $");
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
                            boolean persistent = true;
                            double accTrfBalance = 0;
                            String accTrfTypeName = null;
                            String accTrfNo = null;
                            String accRrfNo = null;
                            // loops until valid account type entered
                            while (persistent) {
                                persistent = false;
                                System.out.println(
                                        ANSI_CYAN + "\nPlease select the account to transfer from: " + ANSI_RESET
                                                + "\n[1] for Checking\n[2] for Savings");
                                accType = sc.nextInt();
                                switch (accType) {
                                    case 1:
                                        accType = checking;
                                        accTrfTypeName = "Checking";
                                        accTrfBalance = checkingBalance;
                                        accTrfNo = CAaccNo;
                                        break;
                                    case 2:
                                        accType = savings;
                                        accTrfTypeName = "Savings";
                                        accTrfBalance = savingsBalance;
                                        accTrfNo = SAaccNo;
                                        break;
                                    default:
                                        System.out.println(ANSI_RED + "Invalid option, please re-enter." + ANSI_RESET);
                                        persistent = true;
                                }
                            }
                            System.out.print(
                                    "\nEnter Account No. to transfer to: #");
                            accRrfNo = sc.next();
                            System.out.print("\nEnter amount to transfer: $");
                            transfer_amount = sc.nextDouble();

                            ResultSet TrfFrom = statement
                                    .executeQuery(
                                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                                    + accTrfNo + " AND account.account_number = " + accTrfNo
                                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
                            while (TrfFrom.next()) {
                                accTrfBalance = TrfFrom.getDouble(25);
                            }

                            if (transfer_amount > accTrfBalance) {
                                System.out.println(ANSI_RED + "\nYour balance is insufficient. Operation Cancelled.\n"
                                        + ANSI_RESET);
                                break;
                            } 
                            else if (transfer_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "\nTransfer amount have to be bigger than $0.\n" + ANSI_RESET);
                                break;
                            } 
                            else if (transfer_amount > 1000000) {
                                System.out
                                        .println(ANSI_RED + "\nExceeded transfer amount of $1,000,000.\n" + ANSI_RESET);
                                break;
                            } else {
                                try {
                                    // Get Transfer To::
                                    String RrfName = null;
                                    double accRrfBalance = 0;
                                    int TrfRetrieve = 0;

                                    ResultSet TrfTo = statement
                                            .executeQuery(
                                                    "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                                            + accRrfNo + " AND account.account_number = " + accRrfNo
                                                            + " ORDER BY transaction_id DESC LIMIT 1;"); //
                                    while (TrfTo.next()) {
                                        RrfName = TrfTo.getString(8);
                                        accRrfBalance = TrfTo.getDouble(25);
                                        TrfRetrieve++;
                                    }
                                    if (TrfRetrieve <= 0){
                                    System.out.printf("\n%sAccount No. %s not found. Please try again.%s\n\n", ANSI_RED,
                                            accRrfNo, ANSI_RESET);
                                    break;
                                    } else {
                                        // Transfer From::
                                        accTrfBalance = accTrfBalance - transfer_amount;
                                        String transferAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                                        PreparedStatement transferAmountBalance = connection
                                                .prepareStatement(transferAmountBalanceQuery);
                                        // transferAmountBalance.setInt(1, 'null');
                                        transferAmountBalance.setString(1, accTrfNo);
                                        transferAmountBalance.setDate(2,
                                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                                        transferAmountBalance.setString(3, "TRF TO ACC " + accRrfNo);
                                        transferAmountBalance.setInt(4, 0);
                                        transferAmountBalance.setDouble(5, transfer_amount);
                                        transferAmountBalance.setDouble(6, 0);
                                        transferAmountBalance.setDouble(7, accTrfBalance);
                                        int rowsTrfAffected = transferAmountBalance.executeUpdate();
                                        if (rowsTrfAffected <= 0) {
                                            System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                                    ANSI_RED, ANSI_RESET);
                                            break;
                                        } else {
                                            TrfTo = statement
                                                    .executeQuery(
                                                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                                                    + accRrfNo + " AND account.account_number = " + accRrfNo
                                                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
                                            while (TrfTo.next()) {
                                                RrfName = TrfTo.getString(8);
                                                accRrfBalance = TrfTo.getDouble(25);
                                            }
                                            // Transfer To::
                                            accRrfBalance = accRrfBalance + transfer_amount;
                                            String transferRrfAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                                            PreparedStatement transferRrfAmountBalance = connection
                                                    .prepareStatement(transferRrfAmountBalanceQuery);
                                            // transferAmountBalance.setInt(1, 'null');
                                            transferRrfAmountBalance.setString(1, accRrfNo);
                                            transferRrfAmountBalance.setDate(2,
                                                    java.sql.Date.valueOf(java.time.LocalDate.now()));
                                            transferRrfAmountBalance.setString(3, "TRF FROM ACC " + accTrfNo);
                                            transferRrfAmountBalance.setInt(4, 0);
                                            transferRrfAmountBalance.setDouble(5, 0);
                                            transferRrfAmountBalance.setDouble(6, transfer_amount);
                                            transferRrfAmountBalance.setDouble(7, accRrfBalance);
                                            int rowsRrfAffected = transferRrfAmountBalance.executeUpdate();
                                            if (rowsRrfAffected > 0) {
                                                TrfFrom = statement
                                                        .executeQuery(
                                                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                                                        + accTrfNo + " AND account.account_number = "
                                                                        + accTrfNo
                                                                        + " ORDER BY transaction_id DESC LIMIT 1;"); //
                                                while (TrfFrom.next()) {
                                                    accTrfBalance = TrfFrom.getDouble(25);
                                                }
                                                System.out.printf(
                                                        "\n%sYou have are successfully transferred $%.2f to Acc No. %s (%s) and your current %s Account Balance is $%.2f.%s\n\n",
                                                        ANSI_CYAN, transfer_amount, accRrfNo, RrfName, accTrfTypeName,
                                                        accTrfBalance, ANSI_RESET);
                                                TrfFrom.close();
                                                TrfTo.close();
                                                transferRrfAmountBalance.close();
                                                transferAmountBalance.close();
                                                break;
                                            } else {
                                                System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                                        ANSI_RED, ANSI_RESET);
                                                double accRrdBalance = accTrfBalance + transfer_amount;
                                                PreparedStatement transferRfdAmountBalance = connection
                                                        .prepareStatement(transferAmountBalanceQuery);
                                                // transferAmountBalance.setInt(1, 'null');
                                                transferRfdAmountBalance.setString(1, accRrfNo);
                                                transferRfdAmountBalance.setDate(2,
                                                        java.sql.Date.valueOf(java.time.LocalDate.now()));
                                                transferRfdAmountBalance.setString(3, "RFD FROM TRF TO ACC " + accTrfNo);
                                                transferRfdAmountBalance.setInt(4, 0);
                                                transferRfdAmountBalance.setDouble(5, 0);
                                                transferRfdAmountBalance.setDouble(6, transfer_amount);
                                                transferRfdAmountBalance.setDouble(7, accRrdBalance);
                                                int rowsRfdAffected = transferRfdAmountBalance.executeUpdate();
                                                if (rowsRfdAffected <= 0) {
                                                    System.out.printf(
                                                            "\n%SsQL Error. Please call the customer service hotline for assistance.%s\n\n",
                                                            ANSI_RED, ANSI_RESET);
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.printf("\n%sAccount No. %s not found. Please try again.%s\n\n", ANSI_RED,
                                            accRrfNo, ANSI_RESET);
                                    break;
                                }
                            }
                        case 3:
                            System.out.println("Your current Balance is $" + totalBalance);
                            break;
                        case 4:
                            System.out.println("Here are your list of accounts");
                            System.out.println("Your current Balance in your checking account is $" + checkingBalance);
                            System.out.println("Your current Balance in your savings account is $" + savingsBalance);
                            break;
                        case 5:
                            persistent = true;
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
                                        System.out.print("Invalid option, please re-enter");
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
                            System.out.println("\n" + ANSI_YELLOW + "Other Services" + ANSI_RESET);
                            System.out.println("--------------------------------\n" +
                                    "| [press 1] : Change Pin Number |\n" +
                                    "--------------------------------");
                            System.out.println("--------------------------------------------\n" +
                                    "| [press 2] : Update Personal Particulars   |\n" +
                                    "--------------------------------------------");
                            System.out.println("-------------------------------\n" +
                                    "| [press 3] : Apply Bank Loan  |\n" +
                                    "-------------------------------");
                            System.out.println("-------------------------------\n" +
                                    "| [press 4] : Go back          |\n" +
                                    "-------------------------------");
                            System.out.println("-------------------------------\n" +
                                    "| [press 5] : Terminate / Quit |\n" +
                                    "-------------------------------");
                            choice = sc.nextInt();
                            switch (choice) {
                                case 1:
                                    persistent = true;
                                    while (persistent) {
                                        persistent = false;
                                        System.out.print("\nEnter new pin: ");
                                        String newPin = sc.next();
                                        if (newPin.matches("\\d{6}")) {
                                            // Hash customer's pin with Salt and Pepper
                                            String[] hashAlgo;
                                            try {
                                                hashAlgo = PinHash.hashPin(newPin);
                                                String newHashedPin = hashAlgo[0];
                                                String newSalt = hashAlgo[1];
                                                // Debug
                                                System.out.println("Random Salt: " + hashAlgo[0]);
                                                System.out.println("Hashed Password: " + hashAlgo[1]);
                                                persistent = true;
                                                while (persistent) {
                                                    System.out.println(
                                                            ANSI_CYAN + "\nProceed with pin change? " + ANSI_RESET
                                                                    + "\n[1] Yes\n[2] No"
                                                                    + ANSI_RESET);
                                                    int pinOp = sc.nextInt();
                                                    persistent = false;
                                                    switch (pinOp) {
                                                        case 1:
                                                            try {
                                                                String npQuery = "UPDATE account SET pin = ?, salt = ? WHERE user_id = ?";
                                                                PreparedStatement npStatement = connection
                                                                        .prepareStatement(npQuery);
                                                                npStatement.setString(1, newHashedPin);
                                                                npStatement.setString(2, newSalt);
                                                                npStatement.setInt(3, pin);
                                                                npStatement.executeUpdate();
                                                                System.out
                                                                        .println(
                                                                                ANSI_CYAN
                                                                                        + "\nYou have successfully changed your pin.\n"
                                                                                        + ANSI_RESET);
                                                            } catch (Exception e) {
                                                                // TODO: handle exception
                                                                System.out.println(
                                                                        ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                                                + ANSI_RESET);
                                                            }
                                                            break;
                                                        case 2:
                                                            System.out.println(ANSI_RED + "\nOperation cancelled.\n"
                                                                    + ANSI_RESET);
                                                            break;
                                                        default:
                                                            System.out.println(
                                                                    ANSI_RED + "Invalid option, please re-enter.\n"
                                                                            + ANSI_RESET);
                                                            persistent = true;
                                                    }
                                                }
                                            } catch (NoSuchAlgorithmException e) {
                                                // TODO Auto-generated catch block
                                                System.out.println(
                                                        ANSI_RED + "\nAn error has occurred, please try again.\n"
                                                                + ANSI_RESET);
                                            }
                                            break;
                                        } else {
                                            System.out.println(
                                                    ANSI_RED + "\nInvalid input. " + ANSI_RESET
                                                            + "Please enter a 6 digit pin: ");
                                            persistent = true;
                                        }
                                    }
                                    break;
                                case 2:
                                    System.out.print("Chose Option 2, WIP");
                                    break;

                                case 3:
                                    double intRate = 3.88;
                                    double proRate = 1.00;
                                    System.out.printf("\nTotal Limit: " + "\n");
                                    System.out.printf("Available Limit: " + "\n");
                                    System.out.printf("Interst Rate: %.2f" + "%% p.a\n", intRate);
                                    System.out.printf("Processing Fee : %.2f" + "%%\n\n", proRate);

                                    persistent = true;
                                    // loops until valid account type entered
                                    while (persistent) {
                                        persistent = false;
                                        System.out.println(
                                                ANSI_CYAN + "Please select the account: " + ANSI_RESET
                                                        + "\n[1] for Checking\n[2] for Savings");
                                        accType = sc.nextInt();
                                        switch (accType) {
                                            case 1:
                                                accType = checking;
                                                break;
                                            case 2:
                                                accType = savings;
                                                break;
                                            default:
                                                System.out.println(ANSI_RED + "Invalid option, " + ANSI_RESET
                                                        + "please re-enter.\n");
                                                persistent = true;
                                        }
                                    }
                                    System.out.print("\nRequested Loan Amount: $");
                                    double loanAmt = sc.nextInt();
                                    int loanType = 0;
                                    persistent = true;
                                    while (persistent) {
                                        persistent = false;
                                        System.out.println(
                                                ANSI_CYAN + "\nPlease select Loan Tenor: " + ANSI_RESET
                                                        + "\n[1] for 3 Months\n[2] for 12 Months\n[3] for 24 Months");
                                        loanType = sc.nextInt();
                                        switch (loanType) {
                                            case 1:
                                                loanType = 3;
                                                break;
                                            case 2:
                                                loanType = 12;
                                                break;
                                            case 3:
                                                loanType = 24;
                                                break;
                                            default:
                                                System.out.print(ANSI_RED + "Invalid option, " + ANSI_RESET
                                                        + "please re-enter.\n");
                                                accType = sc.nextInt();
                                                persistent = true;
                                        }
                                    }
                                    System.out.printf("\nInstallment Per Month for Loan $%.2f/%d Months: $%.2f\n\n",
                                            loanAmt, loanType, (loanAmt / ((Math.pow(1 + (0.0388 / 12), loanType) - 1)
                                                    / ((0.0388 / 12) * (Math.pow(1 + (0.0388 / 12), loanType))))));

                                    persistent = true;
                                    while (persistent) {
                                        System.out.println(
                                                ANSI_CYAN + "Proceed with loan request?  " + ANSI_RESET
                                                        + "\n[1] Yes\n[2] No");
                                        int loanOp = sc.nextInt();
                                        persistent = false;
                                        switch (loanOp) {
                                            case 1:
                                                // Send to db !!!!!!!!!
                                                System.out.println(ANSI_CYAN + "\nLoan request has been processed.\n"
                                                        + ANSI_RESET);
                                                break;
                                            case 2:
                                                System.out.println(ANSI_RED + "\nLoan request cancelled.\n"
                                                        + ANSI_RESET);
                                                break;
                                            default:
                                                System.out.println(ANSI_RED + "Invalid option, " + ANSI_RESET
                                                        + "please re-enter.\n");
                                                persistent = true;
                                        }
                                    }
                                    break;

                                case 4:
                                    break;

                                case 5:
                                    System.out.println("Thank you for banking with SIT ATM");
                                    return;
                            }
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
            SAresultSet.close();
            CAresultSet.close();
            SAloginSet.close();
            CAloginSet.close();
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
            Database db = new Database();
            String query = "SELECT * FROM teller WHERE username=? AND password=?";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            ResultSet resultSet = db.executeQuery(pStatement);
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
            System.out.println("""
                What would you like to do today?
                [1] Create a new customer account
                [2] Add account to an existing customer
                [3] Add card to an existing customer
                [4] Close customer account
                [5] Change customer pin number
                [0] Quit
                """);
            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> {
                        System.out.println("Option [1]: Create a new customer account");
                        CreateNewCustomerAccount();
                    }
                    case 2 -> {
                        System.out.println("Option [2]: Add account to an existing customer");
                        addAccount();
                    }
                    case 3 -> {
                        System.out.println("Option [3]: Add card to an existing customer");
                        addCard();
                    }
                    case 4 -> {
                        System.out.println("Option [4]: Close customer account");
                        closeAccount();
                    }
                    case 5 -> {
                        System.out.println("Option [5]: Change customer's pin");
                        changPin();
                    }
                    case 0 -> {
                        System.out.println("Good bye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice, please try again.");
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
        Customer cust = new Customer();
        Database db = new Database();

        System.out.println("Please enter customer's full name: ");
        cust.setfName(sc.nextLine().toUpperCase());
        System.out.println("Is the customer \n[1] Singaporean/PR or \n[2] Foreigner?");
        int customerType = sc.nextInt();
        while (customerType != 1 && customerType != 2) {
            System.out.println("Invalid selection. Please try again.");
            customerType = sc.nextInt();
        }
        if (customerType == 1){
            String nric;
            boolean isValid = false;

            while (!isValid) {
                System.out.println("Please enter the customer's NRIC: ");
                nric = sc.next().toUpperCase();

                if (Customer.isValidNRIC(nric)) {
                    System.out.println(nric + " is a valid NRIC number.");
                    cust.setNric(nric);
                    isValid = true;
                } else {
                    System.out.println(nric + " is not a valid NRIC number. Please enter a valid NRIC.");
                }
            }
        }else{
            String passNum;
            boolean isValid = false;
            while (!isValid){
                System.out.println("Please enter the customer's passport number: ");
                passNum = sc.next().toUpperCase();

                if (Customer.isValidPassport(passNum)){
                    System.out.println(passNum + " is a valid passport number.");
                    cust.setPnumber(passNum);
                    isValid = true;
                }else{
                    System.out.println(passNum + " is not a valid passport number. Please enter a valid passport number");
                }
            }
        }
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's country: ");
        cust.setCountry(sc.nextLine().toUpperCase());
        System.out.println("Please enter the customer's gender: ");
        cust.setGender(sc.next().toUpperCase());
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's date of birth (YYYY-MM-DD): ");
        cust.setDob(sc.next());
        System.out.println("Please enter the customer's mobile number: ");
        cust.setmNumber(sc.next());
        System.out.println("Please enter the customer's email: ");
        cust.setEmail(sc.next());
        //consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("Please enter the customer's address: ");
        cust.setAddress(sc.nextLine());

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDateTime = sdf.format(dt);


        //Check if customer exist in the database
        try {
            ResultSet resultSet;
            if (customerType == 1){
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE nric=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getNric());;
                resultSet = db.executeQuery(pStatement);

            } else{
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE passport_number=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getPnumber());;
                resultSet = db.executeQuery(pStatement);
            }
            if (resultSet.next()) {
                System.out.println("This account already exist!");
                showTellerMenu();
            }
        }catch (ClassNotFoundException e){
            System.out.println("Error: MySQL driver not found.");
        }catch (SQLException e){
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }

        //Insert customer account into DB
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
            pStatement.setNull(11,Types.DATE);
            if (db.executeUpdate(pStatement) > 0) {
                System.out.println("Customer account has been created successfully!");
                ResultSet resultSet;
                if (customerType == 1){
                    System.out.println("NRIC route");
                    resultSet = db.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE nric='" + cust.getNric() + "'");
                }else{
                    System.out.println("Pnumber route");
                    resultSet = db.executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE passport_number='" + cust.getPnumber() + "'");
                }
                String userId = null;
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                    createAccount(userId);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }

    public static void createAccount(String userId) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Account acc = new Account();
        acc.setUserId(userId);
        Database db = new Database();

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
            acc.setAccountType("Savings Account");
        } else{
            acc.setAccountType("Current Account");
        }
        String bankAccountNumber = null;
        while (bankAccountNumber == null) {
            //Generate 9-digit account number with prefix
            bankAccountNumber = BnkAccNumGen.generateNumber(selection);
            //Debug
            System.out.println("Bank Account Number Generated: " + bankAccountNumber);
            try {
                ResultSet resultSet;
                String query = "SELECT account_number, user_id FROM account where account_number=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, bankAccountNumber);;
                resultSet = db.executeQuery(pStatement);
                if (resultSet.next()) {
                    System.out.println("This account number already exist!" +
                            "\nRegenerating a new account number...");
                    bankAccountNumber = null; //Reset Bank Account Number to null to regenerate a new account number
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
        acc.setAccountNo(bankAccountNumber);
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
        String[] hashAlgo = PinHash.hashPin(pin);
        acc.setPin(hashAlgo[0]);
        acc.setSalt(hashAlgo[1]);
        //String hashedPin = hashAlgo[0];
        //String salt = hashAlgo[1];

        //Debug
        System.out.println("Random Salt: " + hashAlgo[0]);
        System.out.println("Hashed Password: " + hashAlgo[1]);

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);

        //Insert customer account into DB
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
                System.out.println("""
                        Do you want to proceed to card creation?
                        [1] Yes
                        [2] No
                        """);
                int choice = sc.nextInt();
                while (choice != 1 && choice != 2){
                    System.out.println("Invalid selection, please enter your choice: ");
                    choice = sc.nextInt();
                }
                if (choice == 1){
                    createCard(bankAccountNumber);
                }else{
                    showTellerMenu();
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (ClassNotFoundException e){
            System.out.println("Class error: " + e.getMessage());
        }
    }

    public static void createCard(String accNum) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        Card card = new Card();
        card.setAccountNumber(accNum);

        System.out.println("\nCard Creation Process");
        System.out.println("""
               Please select the card type:
               [1] Debit Card
               [2] Credit Card
               """);
        String cardType = null;
        int selection = sc.nextInt();
        while (selection != 1 && selection != 2) {
            System.out.println("Invalid selection. Please try again.");
            selection = sc.nextInt();
        }
        if (selection == 1){
            card.setCardType("Debit Card");
        } else{
            card.setCardType("Credit Card");
        }
        System.out.println("""
                Please select the card network:
                [1] Mastercard
                [2] Visa
                """);
        int cardNetworkSelection = sc.nextInt();
        while (cardNetworkSelection != 1 && cardNetworkSelection != 2) {
            System.out.println("Invalid selection. Please try again.");
            cardNetworkSelection = sc.nextInt();
        }
        //Generate Card Number
        String cardNumber = null;
        while (cardNumber == null) {
            //Generate 16 digit card number depending on Visa or Masters
            cardNumber = CardGenerator.generateCardNumber(cardNetworkSelection);
            //Debug
            System.out.println("ATM Card Number Generated: " + cardNumber);
            try {
                ResultSet resultSet = db.executeQuery("SELECT card_number,account_number FROM card WHERE card_number=" + cardNumber);
                if (resultSet.next()) {
                    System.out.println("This card number already exist!" +
                            "\nRegenerating a new card number...");
                    cardNumber = null; //Reset cardNumber to null to regenerate a new card number
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
        card.setCardNumber(cardNumber);
        //Create Expiry date (+5 years from date of issue)
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.YEAR, 5); // add 5 years
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        //String formattedExpiryDate = sdf.format(expiryDate.getTime());
        card.setExpDate(sdf.format(expiryDate.getTime()));
        //debug
        System.out.println(card.getExpDate());

        //generate CVV
        card.setCVV(CVVGenerator.generateCVV(cardNumber, card.getExpDate()));
        //debug
        System.out.println("CVV:"+card.getCVV());

        //Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);
        try {
            String query = "INSERT INTO card(card_number,account_number,card_type,expiry_date,cvv,created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, card.getCardNumber());
            pStatement.setString(2, card.getAccountNumber());
            pStatement.setString(3, card.getCardType());
            pStatement.setString(4, card.getExpDate());
            pStatement.setString(5, card.getCVV());
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The customer's card is successfully created!");
                showTellerMenu();
            } else {
                System.out.println("Sum ting wong!");
            }
            db.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addAccount(){
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        System.out.println("Please enter customer's current account number: ");
        String accNo = sc.next();
        String userId = null;
        try{
            ResultSet resultSet;
            resultSet = db.executeQuery("SELECT * FROM account where account_number = "+accNo);
            if (resultSet.next()){
                userId = resultSet.getString("user_id");
                createAccount(userId);
            }else{
                System.out.println("""
                        No customer account found!
                        Do you want to create one now?
                        [1] Yes
                        [2] No""");
                int selection = sc.nextInt();
                while (selection != 1 && selection != 2){
                    System.out.println("Invalid selection, please enter your choice: ");
                    selection = sc.nextInt();
                }
                if (selection == 1){
                    CreateNewCustomerAccount();
                }else{
                    showTellerMenu();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithmic Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class Error: "+ e.getMessage());
        }
    }

    public static void addCard(){
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        System.out.println("\n[Notice] The account number entered will be the account tied to the card!");
        System.out.println("Please enter customer's current account number: ");
        String accNo;
        while (true) {
            accNo = sc.next();
            if (accNo.matches("(?=^(250|360)\\d{6}$)\\d{9}")) { // Check if input is a 9-digit number
                break; // Valid input, exit loop
            } else {
                System.out.println("Invalid input. Please enter customer's current account number: ");
            }
        }
        try{
            ResultSet resultSet;
            resultSet = db.executeQuery("SELECT * FROM account where account_number = "+accNo);
            if (resultSet.next()){
                resultSet = db.executeQuery("SELECT account_number FROM card where account_number = "+accNo);
                if (resultSet.next()){
                    System.out.println("[Error] Customer cannot have 2 cards tied to the same account number.");
                    addCard();
                }else{
                    createCard(accNo);
                }
            }else{
                System.out.println("""
                        No customer account found!
                        Do you want to create one now?
                        [1] Yes
                        [2] No""");
                int selection = sc.nextInt();
                while (selection != 1 && selection != 2){
                    System.out.println("Invalid selection, please enter your choice: ");
                    selection = sc.nextInt();
                }
                if (selection == 1){
                    CreateNewCustomerAccount();
                }else{
                    showTellerMenu();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithmic Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class Error: "+ e.getMessage());
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
                String[] hashAlgo = PinHash.hashPin(newPin);
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
