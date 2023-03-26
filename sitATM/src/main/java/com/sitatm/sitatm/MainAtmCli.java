package com.sitatm.sitatm;

import javafx.scene.chart.PieChart;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.io.Console;

public class MainAtmCli {
    private final static int checking = 1;
    private final static int savings = 2;
    private final static String url = "jdbc:mysql://localhost:3306/sitatm";
    private final static String user = "root";
    private final static String pass = "";

    // Color Codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

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

    public static String validator(String accNum, String pin) throws Exception {
        String userId = null;
        String hashedPin;
        String salt;
        System.out.println("Account Number: "+accNum+"\nPin: "+pin);
        Database db = new Database();
        // SQL Connection
        try {
            // Customer table
            ResultSet loginSet = db.executeQuery("SELECT * FROM `account` WHERE `account_number` =" + accNum);
            if (loginSet.next()) {
                hashedPin = loginSet.getString("pin");
                salt = loginSet.getString("salt");
                boolean correctPin = PinHash.hashMatching(pin, salt,hashedPin);
                if (correctPin){
                    userId = loginSet.getString("user_id");
                    showCustomerMenu(accNum, pin, userId);
                }else{
                    System.out.println(ANSI_RED +
                            "Invalid username or password!\n" + ANSI_RESET);
                    CustomerMode();
                }
            }
        } catch (SQLException e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.println(ANSI_RED +
                    "Exception: Invalid username or password!\n" + ANSI_RESET);
            CustomerMode();
        }
        return userId;
    }

    public static String getProfile(String userId, String pin) {
        int count = 0;
        String name = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            ResultSet ProfileSet = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id WHERE account.user_id ="
                                    + userId); //
            while (ProfileSet.next()) {
                name = ProfileSet.getString("full_name");
                count++;
            }
        } catch (Exception e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.println(
                    ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + ANSI_RESET);
            return name;
        }
        if (count > 0) {
            return name;
        } else {
            return null;
        }
    }

    public static String getAccounts(String userId, String accType, String pin) {
        String accNo = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            ResultSet accSet = statement
                    .executeQuery("SELECT * FROM `account` WHERE `user_id` =" + userId
                            + " AND `account_type` = '" + accType + "'");
            while (accSet.next()) {
                accNo = accSet.getString("account_number");
            }
        } catch (Exception e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.println(
                    ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + ANSI_RESET);
            return accNo;
        }
        return accNo;
    }

    public static double getBalanceFromAccount(String accNo, String pin) {
        double balance = 0;
        int count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            ResultSet balanceSet = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accNo + " AND account.account_number = " + accNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
            while (balanceSet.next()) {
                balance = balanceSet.getDouble("balance_amt");
                count++;
                return balance;
            }
        } catch (Exception e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.println(
                    ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + ANSI_RESET);
            return count;
        }
        if (count > 0) {
            return balance;
        }
        return 0;
    }

    public static List<String> selectAccount(Scanner sc, String CAaccNo, String SAaccNo, String action){
        String accType = "checking";
        boolean persistant = true;
        String accTypeName = null;
        List<String> accList = new ArrayList<String>();
        accList.clear();
        // loops until valid account type entered
        while (persistant) {
            persistant = false;
            System.out.println(
                    ANSI_YELLOW + "\nPlease select the account to " + action + " from: " + ANSI_RESET
                            + "\n[1] for Checking\n[2] for Savings");
            accType = sc.next();

            switch (accType) {
                case "1":
                    accType = "checking";
                    accTypeName = "Checking";
                    accList.add(CAaccNo);
                    accList.add(accTypeName);
                    break;
                case "2":
                    accType = "savings";
                    accTypeName = "Savings";
                    accList.add(SAaccNo);
                    accList.add(accTypeName);
                    break;
                default:
                    System.out.println(ANSI_RED + "Invalid option, please re-enter." +
                            ANSI_RESET);
                    persistant = true;
            }
        }
        return accList;
    }

    public static List<Object> retrieveAcc(String accNo, String pin, String accTypeName) {
        List<Object> rList = new ArrayList<Object>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            int count = 0;
            double accReBalance = 0;
            String trfName = null;
            ResultSet AccRetreival = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accNo + " AND account.account_number = " + accNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;"); //
            while (AccRetreival.next()) {
                accReBalance = AccRetreival.getDouble("balance_amt");
                trfName = AccRetreival.getString("full_name");
                count++;
            }
            if (count <= 0) {
                if (accTypeName == "***") {
                    System.out.printf("%sThis account does not exist.%s\n",
                            ANSI_RED, ANSI_RESET);
                    rList.add(false);
                    return rList;
                }
                System.out.printf("\n%s%s account is not opened.%s\n\n",
                        ANSI_RED, accTypeName, ANSI_RESET);
                rList.add(false);
                return rList;
            } else {
                rList.add(true);
                rList.add(accReBalance);
                rList.add(trfName);
                return rList;
            }
        } catch (Exception e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.printf("%sAccount is not found.%s\n",
                    ANSI_RED, ANSI_RESET);
            rList.add(false);
            return rList;
        }
    }

    public static void cDeposit(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double deposit_amount, double accReBalance, String trfName) {
        boolean persistent = true;
        while (persistent) {
            System.out.println(
                    ANSI_CYAN + "\nProceed with " + action + "?" + ANSI_RESET
                            + "\n[1] Yes\n[2] No"
                            + ANSI_RESET);
            String pinOp = sc.next();
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(url, user, pass);

                        accReBalance = accReBalance + deposit_amount;
                        String depositAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                        PreparedStatement depositAmountBalance = connection
                                .prepareStatement(depositAmountBalanceQuery);
                        depositAmountBalance.setString(1, accReNo);
                        depositAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        depositAmountBalance.setString(3, actionStatement);
                        depositAmountBalance.setInt(4, 0);
                        depositAmountBalance.setDouble(5, 0);
                        depositAmountBalance.setDouble(6, deposit_amount);
                        depositAmountBalance.setDouble(7, accReBalance);
                        int rowsDptAffected = depositAmountBalance.executeUpdate();
                        if (rowsDptAffected <= 0) {
                            System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                    ANSI_RED, ANSI_RESET);
                            break;
                        } else {
                            if (action == "deposit" || action == "withdraw") {
                                askReceipt(sc, accReNo, action, actionStatement,
                                        deposit_amount, accTypeName,
                                        accReBalance);
                                receiptPrinter receipt = new receiptPrinter("test", String.valueOf(java.sql.Date.valueOf(LocalDate.now())), userId, 50, deposit_amount, accReBalance, 0);
                                receipt.printReceipt();
                            } else if (action == "transfer") {
                                System.out.printf(
                                        "\n%sYou have successfully transferred $%.2f to Acc No. %s (%s) ",
                                        ANSI_CYAN,
                                        deposit_amount,
                                        accReNo, trfName);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(
                                ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                        + ANSI_RESET);
                        return;
                    }
                    break;
                case "2":
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
    }

    public static void cWithdraw(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double withdraw_amount, double accReBalance, String trfName) {
        boolean persistent = true;
        String pinOp = "0";
        while (persistent) {
            if (action == "deposit" || action == "withdraw") {
                System.out.println(
                        ANSI_CYAN + "\nProceed with " + action + "?" + ANSI_RESET
                                + "\n[1] Yes\n[2] No"
                                + ANSI_RESET);
                pinOp = sc.next();
            } else {
                pinOp = "1";
            }
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(url, user, pass);

                        accReBalance = accReBalance - withdraw_amount;
                        String depositAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                        PreparedStatement depositAmountBalance = connection
                                .prepareStatement(depositAmountBalanceQuery);
                        depositAmountBalance.setString(1, accReNo);
                        depositAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        depositAmountBalance.setString(3, actionStatement);
                        depositAmountBalance.setInt(4, 0);
                        depositAmountBalance.setDouble(5, withdraw_amount);
                        depositAmountBalance.setDouble(6, 0);
                        depositAmountBalance.setDouble(7, accReBalance);
                        int rowsDptAffected = depositAmountBalance.executeUpdate();
                        if (rowsDptAffected <= 0) {
                            System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                    ANSI_RED, ANSI_RESET);
                            break;
                        } else {
                            if (action == "deposit" || action == "withdraw") {
                                askReceipt(sc, accReNo, action, actionStatement,
                                        withdraw_amount, accTypeName,
                                        accReBalance);
                            } else if (action == "transfer") {
                                System.out.printf(
                                        "and your current %s Account Balance is $%.2f.%s\n\n",
                                        accTypeName, accReBalance, ANSI_RESET);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(
                                ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                        + ANSI_RESET);
                    }
                    break;
                case "2":
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
    }

    public static void applyLoan(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double loanAmt, double intRate, double proRate, double lateRate) throws SQLException {
        Database db = new Database();
        String fName = "";
        String email = "";
        ResultSet getInfo = db.executeQuery("SELECT customer.full_name,customer.email FROM customer INNER JOIN account ON customer.user_id = account.user_id WHERE account.account_number = '"+accReNo+"'");
        while (getInfo.next()){
            fName = getInfo.getString("full_name");
            email = getInfo.getString("email");
        }
        List<Object> rList = retrieveAcc(accReNo, pin, accTypeName);
        boolean passedTest = (boolean) rList.get(0);
        if (passedTest) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, pass);
                Statement statement = connection.createStatement();
                int count = 0;
                ResultSet checkLoanIP = statement
                        .executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                        + accReNo + " AND loan.status = 'PENDING'"); //
                while (checkLoanIP.next()) {
                    count++;
                }
                checkLoanIP = statement
                        .executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                        + accReNo + " AND loan.status = 'APPROVED'"); //
                while (checkLoanIP.next()) {
                    count++;
                }

                if (count > 0) {
                    System.out.printf("\n%s%s account has an ongoing loan.%s\n\n",
                            ANSI_RED, accTypeName, ANSI_RESET);
                } else {
                    String loanTypeOp;
                    int loanType = 0;
                    boolean persistent = true;
                    while (persistent) {
                        persistent = false;
                        System.out.println(
                                ANSI_CYAN + "\nPlease select Loan Tenor: " + ANSI_RESET
                                        + "\n[1] for 3 Months\n[2] for 12 Months\n[3] for 24 Months");
                        loanTypeOp = sc.next();
                        switch (loanTypeOp) {
                            case "1":
                                loanType = 3;
                                break;
                            case "2":
                                loanType = 12;
                                break;
                            case "3":
                                loanType = 24;
                                break;
                            default:
                                System.out.print(ANSI_RED + "Invalid option, "
                                        + "please re-enter.\n" + ANSI_RESET);
                                persistent = true;
                        }
                    }
                    double calcDebt = (loanAmt / ((Math.pow(1 + ((intRate / 100) / 12), loanType) - 1)
                            / (((intRate / 100) / 12) * (Math.pow(1 + ((intRate / 100) / 12), loanType)))));
                    System.out.printf("\nInstallment Per Month for Loan $%.2f/%d Months: $%.2f\n\n",
                            loanAmt, loanType, calcDebt);

                    persistent = true;
                    while (persistent) {
                        System.out.println(
                                ANSI_CYAN + "Proceed with loan request?  " + ANSI_RESET
                                        + "\n[1] Yes\n[2] No");
                        String loanOp = sc.next();
                        persistent = false;
                        switch (loanOp) {
                            case "1":
                                try {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    calcDebt = Double.valueOf(df.format(calcDebt));

                                    String depositAmountBalanceQuery = "INSERT INTO `loan`(`loan_id`, `account_number`, `principle_amt`, `interest_rate`, `duration`, `debt`, `date_created`, `repayment_date`, `status`) VALUES (?,?,?,?,?,?,?,?,?);";
                                    PreparedStatement depositAmountBalance = connection
                                            .prepareStatement(depositAmountBalanceQuery);
                                    depositAmountBalance.setInt(1, 0);
                                    depositAmountBalance.setString(2, accReNo);
                                    depositAmountBalance.setDouble(3, loanAmt);
                                    depositAmountBalance.setDouble(4, intRate);
                                    depositAmountBalance.setInt(5, loanType);
                                    depositAmountBalance.setDouble(6, calcDebt * loanType);
                                    depositAmountBalance.setDate(7,
                                            java.sql.Date.valueOf(java.time.LocalDate.now()));
                                    depositAmountBalance.setDate(8, null);
                                    depositAmountBalance.setString(9, "PENDING");
                                    int rowsDptAffected = depositAmountBalance.executeUpdate();
                                    if (rowsDptAffected <= 0) {
                                        System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                                ANSI_RED, ANSI_RESET);
                                        break;
                                    } else {
                                        System.out.println(ANSI_CYAN + "\nLoan request has been processed.\n"
                                                + ANSI_RESET);
                                        Email.sendLoanApplicationEmail(fName,email);
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println(
                                            ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                    + ANSI_RESET);
                                }
                                break;
                            case "2":
                                System.out.println(ANSI_RED + "\nLoan request cancelled.\n"
                                        + ANSI_RESET);
                                break;
                            default:
                                System.out.println(ANSI_RED + "Invalid option, "
                                        + "please re-enter.\n" + ANSI_RESET);
                                persistent = true;
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println("Error: " + e.getMessage());
                System.out.println(
                        ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                + ANSI_RESET);
                return;
            }
        }
    }

    public static void printLatestTransactions(String accStatNo, String pin, String accStatType) {
        String accStat = "";
        double accStatD = 0;
        double accStatW = 0;
        double accStatBalance = 0;
        int count = 0;
        System.out.println("\nShowing (5) latest transactions:");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            ResultSet StatRetreival = statement
                    .executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accStatNo + " AND account.account_number = " + accStatNo
                                    + " ORDER BY transaction_id DESC LIMIT 5;"); //

            String leftAlignFormat = "| %-44s | %-12.2f | %-12.2f | %-12.2f |%n";
            System.out.format(
                    "+----------------------------------------------+--------------+--------------+--------------+%n");
            System.out.format(
                    "| Transactions                                 | Desposit     | Withdrawal   | Balance      |%n");
            System.out.format(
                    "+----------------------------------------------+--------------+--------------+--------------+%n");

            while (StatRetreival.next()) {
                accStat = StatRetreival.getString("transaction_details");
                accStatD = StatRetreival.getDouble("deposit_amt");
                accStatW = StatRetreival.getDouble("withdrawal_amt");
                accStatBalance = StatRetreival.getDouble("balance_amt");
                count++;
                System.out.format(leftAlignFormat, accStat, accStatD,
                        accStatW,
                        accStatBalance);
                System.out.format(
                        "+----------------------------------------------+--------------+--------------+--------------+%n");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println(
                    ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + ANSI_RESET);
            return;
        }
    }

    public static void askReceipt(Scanner sc, String accReNo, String action, String actionStatement,
            double transaction_amount, String accTypeName,
            double accReBalance) {
        boolean persistent = true;
        while (persistent) {
            System.out.println(
                    ANSI_CYAN + "\nPrint Receipt? " + ANSI_RESET
                            + "\n[1] Yes\n[2] No"
                            + ANSI_RESET);
            String pinOp = sc.next();
            persistent = false;
            switch (pinOp) {
                case "1":
                    printReceipt(accReNo, action, actionStatement,
                            transaction_amount, accTypeName,
                            accReBalance);
                    break;
                case "2":
                    noReceipt(action, transaction_amount, accTypeName, accReBalance);
                    break;
                default:
                    System.out.println(
                            ANSI_RED + "Invalid option, please re-enter.\n"
                                    + ANSI_RESET);
                    persistent = true;
            }
        }
    }

    public static void printReceipt(String accReNo, String action, String actionStatement, double transaction_amount,
            String accTypeName, double accReBalance) {
        String strReFormat = "| %-20s   %-12s |%n";
        String leftReFormat = "| %-20s   %-12.2f |%n";
        System.out.println();
        System.out.format(
                "+-------------------------------------+%n");
        System.out.format(
                "|               Receipt               |%n");
        System.out.format(
                "+-------------------------------------+%n");
        System.out.format(
                strReFormat, "Account No.",
                accReNo);
        System.out.format(
                "|                                     |%n");
        System.out.format(leftReFormat,
                actionStatement,
                transaction_amount);
        System.out.format(leftReFormat, "Account Balance",
                accReBalance);
        System.out.format(
                "+-------------------------------------+%n");
        System.out.println();
        noReceipt(action, transaction_amount, accTypeName, accReBalance);
    }

    public static void noReceipt(String action, double transaction_amount, String accTypeName, double accReBalance) {
        System.out.printf(
                "\n%sYou have are successfully %s $%.2f and your current %s Account Balance is $%.2f.%s\n\n",
                ANSI_CYAN,
                action,
                transaction_amount,
                accTypeName,
                accReBalance, ANSI_RESET);
    }

    public static void changePin(Scanner sc, String userId) {
        Console console = System.console();
        boolean persistent = true;
        while (persistent) {
            persistent = false;
            System.out.print("\nEnter new 6 Digit Pin: ");
            String newPin = sc.nextLine();
            char[] enteredPin = console.readPassword();
            newPin = String.valueOf(enteredPin);

            if (newPin.matches("\\d{6}")) {
                // Hash customer's pin with Salt and Pepper
                String[] hashAlgo;
                try {
                    hashAlgo = PinHash.hashPin(newPin);
                    String newHashedPin = hashAlgo[1];
                    String newSalt = hashAlgo[0];
                    // Debug
                    System.out.println("Random Salt: " + hashAlgo[1]);
                    System.out.println("Hashed Password: " + hashAlgo[0]);
                    persistent = true;
                    while (persistent) {
                        System.out.println(
                                ANSI_CYAN + "\nProceed with pin change? " + ANSI_RESET
                                        + "\n[1] Yes\n[2] No"
                                        + ANSI_RESET);
                        String pinOp = sc.next();
                        persistent = false;
                        switch (pinOp) {
                            case "1":
                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection connection = DriverManager.getConnection(url, user, pass);

                                    String npQuery = "UPDATE account SET pin = ?, salt = ? WHERE user_id = ?";
                                    PreparedStatement npStatement = connection
                                            .prepareStatement(npQuery);
                                    npStatement.setString(1, newHashedPin);
                                    npStatement.setString(2, newSalt);
                                    npStatement.setString(3, userId);
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
                            case "2":
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
    }

    public static void CustomerMode() throws Exception {
        // need to change userid to cc no.?
        // pin login by decryptng hash
        // hide pin input
        // withdrawal/deposit in multiples of 10/50 only.
        // limit to withdrawal/deposit
        // error that quits u when int input has alphabets/decimals (int/double)
        // Comments for each functions
        Console console = System.console();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Your Account Number: ");
        String accNum = sc.next();
        System.out.println("Please Enter Your Pin: ");
        String pin = sc.nextLine();
        char[] enteredPin = console.readPassword();
        pin = String.valueOf(enteredPin);
        System.out.println();

        validator(accNum, pin);
    }
        
    public static void showCustomerMenu(String accNum, String pin, String userId) throws Exception {
        Scanner sc = new Scanner(System.in);
        // SQL Connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        Statement statement = connection.createStatement();

        String name = null;
        String SAaccNo = null;
        String CAaccNo = null;
        double savingsBalance = 0;
        double checkingBalance = 0;

        name = getProfile(userId, pin);
        SAaccNo = getAccounts(userId, "Savings Account", pin);
        CAaccNo = getAccounts(userId, "Current Account", pin);
        savingsBalance = getBalanceFromAccount(SAaccNo, pin);
        checkingBalance = getBalanceFromAccount(CAaccNo, pin);

        String choice;
        double totalBalance = checkingBalance + savingsBalance;
        double deposit_amount = 0;
        double transfer_amount = 0;
        double withdrawal_amount = 0; // Not sure if we should impose a min withdraw amount
        double loan_amount= 0;

        System.out.println(ANSI_YELLOW + "Hello " + name + ",");
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
            String leftMenuFormat = "| %-10s: %-15s |%n";
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 1]", "Check Balance");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 2]", "Fund Transfer");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 3]", "Cash Withdraw");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 4]", "All Accounts");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 5]", "Deposit Cash");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 6]", "Other Services");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 7]", "Exit or Quit");
            System.out.format(
                    "-------------------------------%n");
            System.out.println();

            choice = sc.next();
            switch (choice) {
                case "1":
                    savingsBalance = getBalanceFromAccount(SAaccNo, pin);
                    checkingBalance = getBalanceFromAccount(CAaccNo, pin);
                    totalBalance = checkingBalance + savingsBalance;
                    System.out.printf("\n%sYour current balance is $%.2f%s\n", ANSI_YELLOW, totalBalance, ANSI_RESET);
                    System.out.println(ANSI_YELLOW + "Last Updated: " + new java.util.Date() + ANSI_RESET);

                    String accAlignFormat = "| %-20s | %-12.2f |%n";
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.format(accAlignFormat, "Total Balance", totalBalance);
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.println();
                    break;

                case "2":
                    String action = "transfer";
                    List<String> accList = selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accTrfNo = accList.get(0);
                    String accRrfNo = null;
                    String accTypeName = accList.get(1);

                    List<Object> trfList = retrieveAcc(accTrfNo, pin, accTypeName);
                    boolean trfpassedTest = (boolean) trfList.get(0);
                    double trfaccReBalance = 0;
                    String trfName = null;

                    List<Object> rrfList = new ArrayList<Object>();
                    boolean rrfpassedTest = false;
                    double rrfaccReBalance = 0;
                    String rrfName = null;

                    if (trfpassedTest) {
                        trfaccReBalance = (double) trfList.get(1);
                        trfName = (String) trfList.get(2);
                        while (!rrfpassedTest) {
                            System.out.print(
                                    "\nEnter Account No. to transfer to: #");
                            accRrfNo = sc.next();
                            rrfList = retrieveAcc(accRrfNo, pin, "***");
                            rrfpassedTest = (boolean) rrfList.get(0);
                        }
                        rrfaccReBalance = (double) rrfList.get(1);
                        rrfName = (String) rrfList.get(2);

                        String actionStatementTo = "TRANSFER TO ACC NO." + accRrfNo;
                        String actionStatementFrom = "TRANSFER FROM ACC NO." + accTrfNo;

                        // Get current available balance
                        double currentBalanceToTransfer = getBalanceFromAccount(accTrfNo, pin);
                        boolean checker = true;
                        while (checker) {
                            checker = false;
                            System.out.print("\nEnter amount to transfer: $");
                            transfer_amount = sc.nextDouble();
                            if (transfer_amount > currentBalanceToTransfer) {
                                System.out
                                        .println(ANSI_RED + "Your balance is insufficient, please re-enter amount."
                                                + ANSI_RESET);
                                checker = true;
                            } else if (transfer_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Transfer amount have to be bigger than $0.00"
                                                + ANSI_RESET);
                                checker = true;
                            } else if (transfer_amount > 1000000) {
                                System.out
                                        .println(ANSI_RED + "Exceeded transfer amount of $1,000,000."
                                                + ANSI_RESET);
                                checker = true;
                            } else {
                                cDeposit(sc, userId, pin, accRrfNo, accTypeName, action, actionStatementFrom,
                                        transfer_amount,
                                        rrfaccReBalance, rrfName);
                                cWithdraw(sc, userId, pin, accTrfNo, accTypeName, action, actionStatementTo,
                                        transfer_amount,
                                        trfaccReBalance, trfName);
                            }
                        }
                    }
                    break;

                case "3":
                    action = "withdraw";
                    String actionStatement = "ATM WITHDRAWAL";
                    accList = selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    List<Object> rList = retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = getBalanceFromAccount(accReNo, pin);
                        System.out.print("\nEnter amount to withdraw: $");
                        withdrawal_amount = sc.nextDouble();
                        while (withdrawal_amount > currentBalanceToWithdraw) {
                            System.out.println(
                                    ANSI_RED + "Your balance is insufficient, please re-enter amount.\n" + ANSI_RESET);
                            System.out.print("Enter amount to withdraw: $");
                            withdrawal_amount = sc.nextDouble();
                        }
                        boolean checker = true;
                        while (checker) {
                            checker = false;
                            if (withdrawal_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Withdrawal amount have to be bigger than $0.00"
                                                + ANSI_RESET);
                                System.out.print("\nEnter amount to withdraw: $");
                                withdrawal_amount = sc.nextDouble();
                                checker = true;
                            }
                        }
                        // withdraw(accNum, pin, withdrawal_amount, totalBalance);
                        cWithdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement, withdrawal_amount,
                                accReBalance, trfName);
                    }
                    break;
                case "4":
                    savingsBalance = getBalanceFromAccount(SAaccNo, pin);
                    checkingBalance = getBalanceFromAccount(CAaccNo, pin);
                    totalBalance = checkingBalance + savingsBalance;
                    System.out.println(ANSI_YELLOW + "\nAs of " + new java.util.Date() + "," + ANSI_RESET);
                    System.out.println("Here are your list of accounts: ");
                    accAlignFormat = "| %-20s | %-12.2f |%n";
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.format(accAlignFormat, "Saving Balance", savingsBalance);
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.format(accAlignFormat, "Checking Balance", checkingBalance);
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.format(accAlignFormat, "Total Balance", totalBalance);
                    System.out.format(
                            "+----------------------+--------------+%n");
                    System.out.println();
                    break;
                case "5":
                    action = "deposit";
                    actionStatement = "ATM DEPOSIT";
                    accList = selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    rList = retrieveAcc(accReNo, pin, accTypeName);
                    passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        System.out.print("\nEnter amount to deposit: $");
                        deposit_amount = sc.nextDouble();
                        boolean checker = true;
                        while (checker) {
                            checker = false;
                            if (deposit_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Deposit amount have to be bigger than $0.00\n"
                                                + ANSI_RESET);
                                System.out.print("\nEnter amount to deposit: $");
                                deposit_amount = sc.nextDouble();
                                checker = true;
                            }
                        }
                        cDeposit(sc, userId, pin, accReNo, accTypeName, action, actionStatement, deposit_amount,
                                accReBalance,
                                trfName);
                    }
                    break;
                case "6":
                    showCustomerSubMenu(accNum, pin, userId, CAaccNo, SAaccNo);
                    break;
                case "7":
                    System.out.println("\nThank you for banking with SIT ATM.\n");
                    System.exit(0);
            }
        }
    }

    public static void showCustomerSubMenu(String accNum, String pin, String userId, String CAaccNo, String SAaccNo)
            throws Exception {
        // SQL Connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        Statement statement = connection.createStatement();

        System.out.println("\n" + ANSI_YELLOW + "Other Services" + ANSI_RESET);
        String leftSubMenuFormat = "| %-10s: %-28s |%n";
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 1]", "Change Pin Number");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 2]", "Update Personal Particulars");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 3]", "Pay Bills");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 4]", "Apply Bank Loan");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 5]", "Show Latest Transactions");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 6]", "Go back");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 7]", "Terminate / Quit");
        System.out.format(
                "--------------------------------------------%n");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        String xchoice = sc.next();
        switch (xchoice) {
            case "1":
                changePin(sc, userId);
                break;

            case "2":
                System.out.print("Chose Option 2, WIP");
                break;

            case "3":
                // when approved, set repayment date by 1 month from approval
                String action = "repay loan";
                String actionStatement = "LOAN REPAYMENT";
                List<String> accList = selectAccount(sc, CAaccNo, SAaccNo, action);
                String accReNo = accList.get(0);
                String accTypeName = accList.get(1);

                int loan_id = 0;
                double principleAmt = 0;
                double interestRate = 0;
                int duration = 0;
                double debt = 0;
                double currentDebt = 0;
                java.sql.Date date_created = null;
                java.sql.Date repayment_date = null;
                String status = null;
                int count = 0;

                ResultSet checkLoanIP = statement
                        .executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                        + accReNo + " AND loan.status = 'APPROVED'"); //
                while (checkLoanIP.next()) {
                    loan_id = checkLoanIP.getInt("loan_id");
                    principleAmt = checkLoanIP.getDouble("principle_amt");
                    interestRate = checkLoanIP.getDouble("interest_rate");
                    duration = checkLoanIP.getInt("duration");
                    debt = checkLoanIP.getDouble("debt");
                    date_created = checkLoanIP.getDate("date_created");
                    repayment_date = checkLoanIP.getDate("repayment_date");
                    status = checkLoanIP.getString("status");
                    count++;
                }
                if (count <= 0) {
                    System.out.printf("\n%s%s account does not have an ongoing approved loan.%s\n\n",
                            ANSI_RED, accTypeName, ANSI_RESET);
                } else {
                    currentDebt = debt;
                    System.out.printf(
                            "\n%sLoan retrieved.%s\nPrinciple Amount:%.2f\nCurrent Interest Rate:%.2f\nLoan Tenor Left:%d\nNext Repayment Date:%s\nThis Month's Repayment:%.2f\nTotal Debt:%.2f\n\n",
                            ANSI_YELLOW, ANSI_RESET, principleAmt, interestRate, duration, repayment_date,
                            currentDebt / duration, currentDebt);

                    java.sql.Date todayDate = java.sql.Date.valueOf(java.time.LocalDate.now());
                    while (todayDate.after(repayment_date)) {
                        // Penatly for late repayment, recal debt
                        interestRate += 5.00;
                        if (duration > 1) {
                            repayment_date = java.sql.Date
                                    .valueOf(repayment_date.toLocalDate().plusMonths(1));
                            duration -= 1;
                        } else {
                            duration = 1;
                            status = "OVERDUED";
                        }
                        currentDebt *= ((100 + interestRate) / 100);
                        System.out.printf(
                                "\n%sLoan dued.%s\nPrinciple Amount:%.2f\nCurrent Interest Rate:%.2f\nLoan Tenor Left:%d\nNext Repayment Date:%s\nThis Month's Repayment:%.2f\nDebt:%.2f\n\n",
                                ANSI_YELLOW, ANSI_RESET, principleAmt, interestRate, duration,
                                repayment_date,
                                currentDebt / duration, currentDebt);
                    }

                    action = "repay loan";
                    actionStatement = "LOAN REPAYMENT";
                    accList = selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    double currentMonthDebt = currentDebt / duration;

                    List<Object> payList = retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) payList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) payList.get(1);
                        String trfName = (String) payList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = getBalanceFromAccount(accReNo, pin);
                        System.out.printf("\nAmount to repay: $%.2f", currentMonthDebt);
                        if (currentMonthDebt > currentBalanceToWithdraw) {
                            System.out.println(
                                    ANSI_RED + "Your balance is insufficient, operation cancelled.\n"
                                            + ANSI_RESET);
                            return;
                        } else {
                            DecimalFormat df = new DecimalFormat("#.##");
                            debt = Double.valueOf(df.format(debt));

                            // if debt is fully paid for this month, set month to next and duration -1
                            repayment_date = java.sql.Date
                                    .valueOf(repayment_date.toLocalDate().plusMonths(1));
                            if (duration > 1) {
                                duration -= 1;
                            } else {
                                status = "CLOSED";
                                duration -= 1;
                            }
                            debt -= currentMonthDebt;

                            cWithdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                    currentMonthDebt,
                                    accReBalance, trfName);
                            String updateLoanQuery = "UPDATE `loan` SET `principle_amt`=?,`interest_rate`=?,`duration`=?,`debt`=?,`repayment_date`=?,`status`=? WHERE `loan_id` = ?";
                            PreparedStatement updateLoanStatement = connection
                                    .prepareStatement(updateLoanQuery);
                            updateLoanStatement.setDouble(1, principleAmt);
                            updateLoanStatement.setDouble(2, interestRate);
                            updateLoanStatement.setInt(3, duration);
                            updateLoanStatement.setDouble(4, debt);
                            updateLoanStatement.setDate(5, repayment_date);
                            updateLoanStatement.setString(6, status);
                            updateLoanStatement.setInt(7, loan_id);
                            int rowsLoanAffected = updateLoanStatement.executeUpdate();
                            if (rowsLoanAffected > 0) {
                                System.out.println("\n\nLoan repayment has successfully proccessed.");
                            }
                        }
                    }
                }
                break;
            case "4":
                String accLoanNo = null;
                String accLoanTypeName = null;
                double intRate = 3.88;
                double proRate = 1.00;
                double lateRate = 5;
                System.out.printf("\nTotal Limit: " + "\n");
                System.out.printf("Available Limit: " + "\n");
                System.out.printf("Interst Rate: %.2f" + "%% p.a\n", intRate);
                System.out.printf("Processing Fee : %.2f" + "%%\n\n", proRate);
                System.out.printf("Late Repayement Fee : %.2f" + "%%\n\n", lateRate);

                action = "loan application";
                actionStatement = "LOAN APPLICATION";
                accList = selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
                accLoanNo = accList.get(0);
                accLoanTypeName = accList.get(1);

                boolean checker = true;
                double loan_amount = 0;
                while (checker) {
                    checker = false;
                    System.out.print("\nRequested Loan Amount: $");
                    loan_amount = sc.nextDouble();
                    if (loan_amount <= 0) {
                        System.out
                                .println(ANSI_RED
                                        + "\nLoan amount have to be bigger than $0.00\n"
                                        + ANSI_RESET);
                        checker = true;
                    }
                }

                applyLoan(sc, userId, pin, accLoanNo, accLoanTypeName, action, actionStatement, loan_amount,
                        intRate, proRate, lateRate);
                break;

            case "5":
                actionStatement = "PRINT LATEST TRANSACTIONS";
                accList = selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
                String accStatNo = accList.get(0);
                String accStatType = accList.get(1);
                List<Object> pList = retrieveAcc(accStatNo, pin, accStatType);
                boolean passedTest = (boolean) pList.get(0);
                if (passedTest) {
                    printLatestTransactions(accStatNo, pin, accStatType);
                }
                break;

            case "6":
                showCustomerMenu(accNum, pin, userId);
                break;

            case "7":
                System.out.println("\nThank you for banking with SIT ATM.\n");
                System.exit(0);
        }
    }
    

    public static void TellerMode() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n" +
                "Please enter your username: ");
        String username = sc.next();
        Console console = System.console();
        char[] passwordArray = console.readPassword("Enter your  password: ");
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
                [6] Loan Approval
                [7] [TEST FUNCTION] Send Welcome Email
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
                    case 6 -> {
                        System.out.println("Option [6]: Loan approval");
                        loanApproval();
                    }
                    case 7 -> {
                        System.out.println("Option [7]: Send Welcome Email\n[NOTICE] THIS IS A EXPERIMENTAL FEATURE!");
                        //Email.sendEmailPrep("250404701",1);
                        Email.sendEmailPrep("250404701",2);
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
        //System.out.println("Please enter the customer's mobile number: ");
        //cust.setmNumber(sc.next());
        //String mNum = sc.next();
        boolean isValid = false;
        while (!isValid){
            System.out.println("Please enter the customer's mobile number: ");
            cust.setmNumber(sc.next());
            if (Customer.isValidMobileNumber(cust.getmNumber())){
                System.out.println("Valid mobile number");
                isValid = true;
            }else{
                System.out.println("Invalid mobile number, please enter a valid mobile number.");
            }
        }
        //System.out.println("Please enter the customer's email: ");
        //cust.setEmail(sc.next());
        isValid = false;
        while (!isValid){
            System.out.println("Please enter the customer's email: ");
            cust.setEmail(sc.next());
            if (Customer.isValidEmail(cust.getEmail())){
                System.out.println("Valid email");
                isValid = true;
            }else{
                System.out.println("Invalid email address, please enter a valid email address.");
            }
        }
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
        acc.setPin(hashAlgo[1]);
        acc.setSalt(hashAlgo[0]);
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
                Email.sendEmailPrep(acc.getAccountNo(),1);
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
                Email.sendEmailPrep(accNum,2);
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
            if(accountDeactivated(accountNumber)){
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
            if (accountDeactivated(accNo)){
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

    public static void showAllAccounts(String accNum, String pin) throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "SELECT a.account_type, a.account_number, t.balance_amt FROM account a JOIN customer c ON a.user_id = c.user_id LEFT JOIN ( SELECT account_number, balance_amt FROM transaction t1 WHERE t1.transaction_id = ( SELECT MAX(t2.transaction_id) FROM transaction t2 WHERE t2.account_number = t1.account_number) ) t ON a.account_number = t.account_number WHERE c.user_id = ( SELECT user_id FROM account WHERE account_number = ? AND pin = ? ) ";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, accNum);
            pStatement.setString(2, pin);
            ResultSet resultSet = pStatement.executeQuery();
            // iterate through the result set and print out the values in the desired format
            while (resultSet.next()) {
                String accountType = resultSet.getString("account_type");
                double balanceAmt = Double.parseDouble(resultSet.getString("balance_amt"));
                System.out.printf("Your current balance in your %s account is $%.2f\n", ANSI_YELLOW +accountType +ANSI_RESET, balanceAmt);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL class not found. Details: \n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }

    public static void deposit(String accNum, String pin, double amount, double balance) throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "UPDATE transaction t INNER JOIN account a ON t.account_number = a.account_number SET t.balance_amt = ? + ? WHERE a.account_number = ? AND a.pin = ? ";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, String.valueOf(balance));
            pStatement.setString(2, String.valueOf(amount));
            pStatement.setString(3, accNum);
            pStatement.setString(4, pin);
            int rowsUpdated = pStatement.executeUpdate();
            if (rowsUpdated > 0) {
                String query2 = "SELECT balance_amt FROM transaction WHERE account_number = ? ORDER BY transaction_id DESC LIMIT 1";
                PreparedStatement pStatement2 = connection.prepareStatement(query2);
                pStatement2.setString(1, accNum);
                ResultSet resultSet2 = pStatement2.executeQuery();
                // iterate through the result set and print out the values in the desired format
                while (resultSet2.next()) {
                    Double balanceAmt = Double.parseDouble(resultSet2.getString("balance_amt"));
                    System.out.printf("You have successfully deposited $%.2f. \nYour current balance account is: $%.2f" , amount, balanceAmt);
                }
                resultSet2.close();
                pStatement2.close();
            } else {
                System.out.println("Deposit failed. Please check account number and PIN.");
            }
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL class not found. Details: \n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }

    public static double showBalance(String accNum, String pin) throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            double balance = 0;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer c JOIN account a ON c.user_id = a.user_id JOIN ( SELECT * FROM transaction WHERE account_number = " + accNum + " ORDER BY date DESC LIMIT 1) t ON a.account_number = t.account_number WHERE a.account_number = " + accNum + " AND a.pin = " + pin);
            while (resultSet.next()) {
                balance = resultSet.getDouble("balance_amt");
                System.out.printf("Your current Balance is $%.2f\n", balance);
                return balance;
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL class not found. Details: \n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
        return 0;
    }

    public static void withdraw(String accNum, String pin, double amount, double balance) throws ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String query = "UPDATE transaction t INNER JOIN account a ON t.account_number = a.account_number SET t.balance_amt = ? - ? WHERE a.account_number = ? AND a.pin = ? ";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, String.valueOf(balance));
            pStatement.setString(2, String.valueOf(amount));
            pStatement.setString(3, accNum);
            pStatement.setString(4, pin);
            int rowsUpdated = pStatement.executeUpdate();
            if (rowsUpdated > 0) {
                String query2 = "SELECT balance_amt FROM transaction WHERE account_number = ? ORDER BY transaction_id DESC LIMIT 1";
                PreparedStatement pStatement2 = connection.prepareStatement(query2);
                pStatement2.setString(1, accNum);
                ResultSet resultSet2 = pStatement2.executeQuery();
                // iterate through the result set and print out the values in the desired format
                while (resultSet2.next()) {
                    double balanceAmt = Double.parseDouble(resultSet2.getString("balance_amt"));
                    System.out.printf("You have successfully withdrawed $%.2f. \nYour current account balance is: $%.2f.\n" , amount, balanceAmt);
                }
                resultSet2.close();
                pStatement2.close();
            } else {
                System.out.println("Withdraw failed. Please check account number and PIN.");
            }
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL class not found. Details: \n" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }
    public static void loanApproval() throws SQLException {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        String fName = "";
        String email = "";
        try {
            ResultSet resultSet = db.executeQuery("SELECT * FROM loan WHERE status = 'PENDING'");
            ResultSetMetaData metaData = resultSet.getMetaData();

            // Get the number of columns in the ResultSet
            int numColumns = metaData.getColumnCount();
                    
            // Print out the column names
            for (int i = 1; i <= numColumns; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println();
            
            // Print out the rows (if any)
            boolean hasRows = false;
            while (resultSet.next()) {
                hasRows = true;
                for (int i = 1; i <= numColumns; i++) {
                    System.out.printf("%-20s", resultSet.getString(i));
                }
                System.out.println();
            }
            double requestedAmount = 0.0;
            int loanID = 0;
            if (hasRows) {
                System.out.println("\nPlease select the account you want to approve: ");
                String accNum = sc.next();

                ResultSet getInfo = db.executeQuery("SELECT customer.full_name,customer.email FROM customer INNER JOIN account ON customer.user_id = account.user_id WHERE account.account_number = '"+accNum+"'");
                while (getInfo.next()){
                    fName = getInfo.getString("full_name");
                    email = getInfo.getString("email");
                }
                ResultSet approvalResultSet = db.executeQuery("SELECT * FROM loan WHERE status = 'PENDING' and account_number = '"+accNum+"'");
                if (approvalResultSet.next()){
                    requestedAmount = Double.parseDouble(approvalResultSet.getString("principle_amt"));
                    String intRate = approvalResultSet.getString("interest_rate");
                    int duration = Integer.parseInt(approvalResultSet.getString("duration"));
                    double debt = Double.parseDouble(approvalResultSet.getString("debt"));
                    double repayment = debt/duration;
                    loanID = approvalResultSet.getInt("loan_id");
                    String status;
                    if (!loan.approveLoan(requestedAmount)) {
                        status = "DECLINED";
                        try {
                            String query = "UPDATE loan SET status = ?, repayment_date = ? WHERE account_number = ? AND loan_id = ?";
                            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                            pStatement.setString(1, status);
                            pStatement.setString(2, accNum);
                            pStatement.setInt(3, loanID);
                            int rowsAffected = pStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Sorry, the loan application is declined.\n");
                                Email.sendLoanRejectionEmail(fName,email);
                                showTellerMenu();
                            }
                        } catch (SQLException e){
                            System.out.println("SQL Exception caught: "+e);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println("No Such Algorithm Exception caught: "+e);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Runtime Exception caught: "+e);
                        }
                    }
                    else{
                        status = "APPROVED";
                        // Get today's date
                        LocalDate today = LocalDate.now();
                        // Add one month to today's date
                        LocalDate repaymentDate = today.plusMonths(1);
                        try {
                            String query = "UPDATE loan SET status = ?, repayment_date = ? WHERE account_number = ? AND loan_id = ?";
                            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                            pStatement.setString(1,status);
                            pStatement.setDate(2, java.sql.Date.valueOf(repaymentDate));
                            pStatement.setString(3,accNum);
                            pStatement.setInt(4,loanID);
                            int rowsAffected = pStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("The loan has been approved!\n");
                                Email.sendLoanApprovalEmail(fName,email,requestedAmount,intRate,duration,repayment);
                                showTellerMenu();
                            }
                        }catch (SQLException e){
                            System.out.println("SQL Exception caught: "+e);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println("No Such Algorithm Exception caught: "+e);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Runtime Exception caught: "+e);
                        }
                    }
                }
            }else {
                System.out.println("\nNo loans to be approved, Looks like it's a quite day huh...\n");
            }
        }catch (SQLException e){
            System.out.println("SQL Exception caught: "+e);
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
