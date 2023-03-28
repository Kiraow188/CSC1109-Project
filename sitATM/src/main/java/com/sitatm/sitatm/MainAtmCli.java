package com.sitatm.sitatm;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class CustomerLogin {
    public static String validator(String accNum, String pin) throws Exception {
        String userId = null;
        String hashedPin;
        String salt;
        System.out.println("Account Number: " + accNum + "\nPin: " + pin);
        Database db = new Database();
        // SQL Connection
        try {
            // Customer table
            ResultSet loginSet = db.executeQuery("SELECT * FROM `account` WHERE `account_number` =" + accNum);
            if (loginSet.next()) {
                hashedPin = loginSet.getString("pin");
                salt = loginSet.getString("salt");
                boolean correctPin = PinHash.hashMatching(pin, salt, hashedPin);
                if (correctPin) {
                    userId = loginSet.getString("user_id");
                    MainAtmCli.showCustomerMenu(accNum, pin, userId);
                } else {
                    System.out.println(
                            MainAtmCli.ANSI_RED +
                                    "Invalid username or password!\n" + MainAtmCli.ANSI_RESET);
                    MainAtmCli.CustomerMode();
                }
            }
        } catch (SQLException e) {
            // System.out.println("Error: " + e.getMessage());
            System.out.println(
                    MainAtmCli.ANSI_RED +
                            "Exception: Invalid username or password!\n" + MainAtmCli.ANSI_RESET);
            MainAtmCli.CustomerMode();
        }
        return userId;
    }
}

class CustomerStat {
    public static String getProfile(String userId, String pin) {
        int count = 0;
        String name = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                    MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + MainAtmCli.ANSI_RESET);
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
            Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                    MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + MainAtmCli.ANSI_RESET);
            return accNo;
        }
        return accNo;
    }

    public static double getBalanceFromAccount(String accNo, String pin) {
        double balance = 0;
        int count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                    MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + MainAtmCli.ANSI_RESET);
            return count;
        }
        if (count > 0) {
            return balance;
        }
        return 0;
    }

    public static List<String> selectAccount(Scanner sc, String CAaccNo, String SAaccNo, String action) {
        String accType = "checking";
        boolean persistant = true;
        String accTypeName = null;
        List<String> accList = new ArrayList<String>();
        accList.clear();
        // loops until valid account type entered
        while (persistant) {
            persistant = false;
            System.out.println(
                    MainAtmCli.ANSI_YELLOW + "\nPlease select the account to " + action + " from: "
                            + MainAtmCli.ANSI_RESET
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
                    System.out.println(
                            MainAtmCli.ANSI_RED + "Invalid option, please re-enter." +
                                    MainAtmCli.ANSI_RESET);
                    persistant = true;
            }
        }
        return accList;
    }

    public static List<Object> retrieveAcc(String accNo, String pin, String accTypeName) {
        List<Object> rList = new ArrayList<Object>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                            MainAtmCli.ANSI_RED, MainAtmCli.ANSI_RESET);
                    rList.add(false);
                    return rList;
                }
                System.out.printf("\n%s%s account is not opened.%s\n\n",
                        MainAtmCli.ANSI_RED, accTypeName, MainAtmCli.ANSI_RESET);
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
                    MainAtmCli.ANSI_RED, MainAtmCli.ANSI_RESET);
            rList.add(false);
            return rList;
        }
    }

}

class CustomerATM {
    public static void cDeposit(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double deposit_amount, double accReBalance, String trfName) {
        boolean persistent = true;
        String pinOp = "0";
        while (persistent) {
            if (action == "deposit" || action == "withdraw") {
                System.out.println(
                        MainAtmCli.ANSI_CYAN + "\nProceed with " + action + "?" + MainAtmCli.ANSI_RESET
                                + "\n[1] Yes\n[2] No" + MainAtmCli.ANSI_RESET);
                pinOp = sc.next();
            } else {
                pinOp = "1";
            }
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user,
                                MainAtmCli.pass);

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
                                    MainAtmCli.ANSI_RED, MainAtmCli.ANSI_RESET);
                            break;
                        } else {
                            if (action == "deposit" || action == "withdraw") {
                                askReceipt(sc, accReNo, action, actionStatement,
                                        deposit_amount, accTypeName,
                                        accReBalance);
                            } else if (action == "transfer") {
                                System.out.printf(
                                        "\n%sYou have successfully transferred $%.2f to Acc No. %s (%s) %s",
                                        MainAtmCli.ANSI_CYAN,
                                        deposit_amount,
                                        accReNo, trfName, MainAtmCli.ANSI_RESET);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(
                                MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                        + MainAtmCli.ANSI_RESET);
                    }
                    break;
                case "2":
                    System.out.println(
                            MainAtmCli.ANSI_RED + "\nOperation cancelled.\n"
                                    + MainAtmCli.ANSI_RESET);
                    break;
                default:
                    System.out.println(
                            MainAtmCli.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + MainAtmCli.ANSI_RESET);
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
                        MainAtmCli.ANSI_CYAN + "\nProceed with " + action + "?" + MainAtmCli.ANSI_RESET
                                + "\n[1] Yes\n[2] No" + MainAtmCli.ANSI_RESET);
                pinOp = sc.next();
            } else {
                pinOp = "1";
            }
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user,
                                MainAtmCli.pass);

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
                                    MainAtmCli.ANSI_RED, MainAtmCli.ANSI_RESET);
                            break;
                        } else {
                            if (action == "deposit" || action == "withdraw") {
                                askReceipt(sc, accReNo, action, actionStatement,
                                        withdraw_amount, accTypeName,
                                        accReBalance);
                            } else if (action == "transfer") {
                                System.out.printf(
                                        "%sand your current %s Account Balance is $%.2f.%s\n\n",
                                        MainAtmCli.ANSI_CYAN, accTypeName, accReBalance, MainAtmCli.ANSI_RESET);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(
                                MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                        + MainAtmCli.ANSI_RESET);
                    }
                    break;
                case "2":
                    System.out.println(
                            MainAtmCli.ANSI_RED + "\nOperation cancelled.\n"
                                    + MainAtmCli.ANSI_RESET);
                    break;
                default:
                    System.out.println(
                            MainAtmCli.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + MainAtmCli.ANSI_RESET);
                    persistent = true;
            }
        }
    }

    public static void applyLoan(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double loanAmt, double intRate, double proRate, double lateRate)
            throws SQLException {
        Database db = new Database();
        String fName = "";
        String email = "";
        ResultSet getInfo = db.executeQuery(
                "SELECT customer.full_name,customer.email FROM customer INNER JOIN account ON customer.user_id = account.user_id WHERE account.account_number = '"
                        + accReNo + "'");
        while (getInfo.next()) {
            fName = getInfo.getString("full_name");
            email = getInfo.getString("email");
        }
        List<Object> rList = CustomerStat.retrieveAcc(accReNo, pin, accTypeName);
        boolean passedTest = (boolean) rList.get(0);
        if (passedTest) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                            MainAtmCli.ANSI_RED, accTypeName, MainAtmCli.ANSI_RESET);
                } else {
                    String loanTypeOp;
                    int loanType = 0;
                    boolean persistent = true;
                    while (persistent) {
                        persistent = false;
                        System.out.println(
                                MainAtmCli.ANSI_CYAN + "\nPlease select Loan Tenor: " + MainAtmCli.ANSI_RESET
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
                                System.out.print(
                                        MainAtmCli.ANSI_RED + "Invalid option, "
                                                + "please re-enter.\n" + MainAtmCli.ANSI_RESET);
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
                                MainAtmCli.ANSI_CYAN + "Proceed with loan request?  " + MainAtmCli.ANSI_RESET
                                        + "\n[1] Yes\n[2] No" + MainAtmCli.ANSI_RESET);
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
                                                MainAtmCli.ANSI_RED, MainAtmCli.ANSI_RESET);
                                        break;
                                    } else {
                                        System.out.println(
                                                MainAtmCli.ANSI_CYAN + "\nLoan request has been processed.\n"
                                                        + MainAtmCli.ANSI_RESET);
                                        Email.sendLoanApplicationEmail(fName, email);
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println(
                                            MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                    + MainAtmCli.ANSI_RESET);
                                }
                                break;
                            case "2":
                                System.out.println(
                                        MainAtmCli.ANSI_RED + "\nLoan request cancelled.\n"
                                                + MainAtmCli.ANSI_RESET);
                                break;
                            default:
                                System.out.println(
                                        MainAtmCli.ANSI_RED + "Invalid option, "
                                                + "please re-enter.\n" + MainAtmCli.ANSI_RESET);
                                persistent = true;
                        }
                    }
                }
            } catch (Exception e) {
                // System.out.println("Error: " + e.getMessage());
                System.out.println(
                        MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                + MainAtmCli.ANSI_RESET);
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
            Connection connection = DriverManager.getConnection(MainAtmCli.url, MainAtmCli.user, MainAtmCli.pass);
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
                    MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + MainAtmCli.ANSI_RESET);
            return;
        }
    }

    public static void askReceipt(Scanner sc, String accReNo, String action, String actionStatement,
            double transaction_amount, String accTypeName,
            double accReBalance) {
        boolean persistent = true;
        while (persistent) {
            System.out.println(
                    MainAtmCli.ANSI_CYAN + "\nPrint Receipt? " + MainAtmCli.ANSI_RESET
                            + "\n[1] Yes\n[2] No" + MainAtmCli.ANSI_RESET);
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
                            MainAtmCli.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + MainAtmCli.ANSI_RESET);
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
                MainAtmCli.ANSI_CYAN,
                action,
                transaction_amount,
                accTypeName,
                accReBalance, MainAtmCli.ANSI_RESET);
    }

    public static void changePin(Scanner sc, String userId) {
        Console console = System.console();
        boolean persistent = true;
        while (persistent) {
            persistent = false;
            System.out.print("\nEnter new 6 Digit Pin: ");
            String newPin = sc.nextLine();
            if (System.console() != null) {
                char[] enteredPin = console.readPassword();
                newPin = String.valueOf(enteredPin);
            }

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
                                MainAtmCli.ANSI_CYAN + "\nProceed with pin change? " + MainAtmCli.ANSI_RESET
                                        + "\n[1] Yes\n[2] No" + MainAtmCli.ANSI_RESET);
                        String pinOp = sc.next();
                        persistent = false;
                        switch (pinOp) {
                            case "1":
                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection connection = DriverManager.getConnection(MainAtmCli.url,
                                            MainAtmCli.user, MainAtmCli.pass);

                                    String npQuery = "UPDATE account SET pin = ?, salt = ? WHERE user_id = ?";
                                    PreparedStatement npStatement = connection
                                            .prepareStatement(npQuery);
                                    npStatement.setString(1, newHashedPin);
                                    npStatement.setString(2, newSalt);
                                    npStatement.setString(3, userId);
                                    npStatement.executeUpdate();
                                    System.out
                                            .println(
                                                    MainAtmCli.ANSI_CYAN
                                                            + "\nYou have successfully changed your pin.\n"
                                                            + MainAtmCli.ANSI_RESET);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println(
                                            MainAtmCli.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                    + MainAtmCli.ANSI_RESET);
                                }
                                break;
                            case "2":
                                System.out.println(
                                        MainAtmCli.ANSI_RED + "\nOperation cancelled.\n"
                                                + MainAtmCli.ANSI_RESET);
                                break;
                            default:
                                System.out.println(
                                        MainAtmCli.ANSI_RED + "Invalid option, please re-enter.\n"
                                                + MainAtmCli.ANSI_RESET);
                                persistent = true;
                        }
                    }
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    System.out.println(
                            MainAtmCli.ANSI_RED + "\nAn error has occurred, please try again.\n"
                                    + MainAtmCli.ANSI_RESET);
                }
                break;
            } else {
                System.out.println(
                        MainAtmCli.ANSI_RED + "\nInvalid input. " + MainAtmCli.ANSI_RESET
                                + "Please enter a 6 digit pin: ");
                persistent = true;
            }
        }
    }
}

public class MainAtmCli {
    private final static int checking = 1;
    private final static int savings = 2;
    protected final static String url = "jdbc:mysql://localhost:3306/sitatm";
    protected final static String user = "root";
    protected final static String pass = "";

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
        if (System.console() != null) {
            char[] enteredPin = console.readPassword();
            pin = String.valueOf(enteredPin);
        }
        System.out.println();

        CustomerLogin.validator(accNum, pin);
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

        name = CustomerStat.getProfile(userId, pin);
        SAaccNo = CustomerStat.getAccounts(userId, "Savings Account", pin);
        CAaccNo = CustomerStat.getAccounts(userId, "Current Account", pin);
        savingsBalance = CustomerStat.getBalanceFromAccount(SAaccNo, pin);
        checkingBalance = CustomerStat.getBalanceFromAccount(CAaccNo, pin);

        String choice;
        double totalBalance = checkingBalance + savingsBalance;
        double deposit_amount = 0;
        double transfer_amount = 0;
        double withdrawal_amount = 0; // Not sure if we should impose a min withdraw amount
        double loan_amount = 0;

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
                    savingsBalance = CustomerStat.getBalanceFromAccount(SAaccNo, pin);
                    checkingBalance = CustomerStat.getBalanceFromAccount(CAaccNo, pin);
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
                    List<String> accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accTrfNo = accList.get(0);
                    String accRrfNo = null;
                    String accTypeName = accList.get(1);

                    List<Object> trfList = CustomerStat.retrieveAcc(accTrfNo, pin, accTypeName);
                    boolean trfpassedTest = (boolean) trfList.get(0);
                    double trfaccReBalance = 0;
                    String trfName = null;

                    List<Object> rrfList = new ArrayList<Object>();
                    boolean rrfpassedTest = false;
                    double rrfaccReBalance = 0;
                    String rrfName = null;

                    boolean checker = true;

                    if (trfpassedTest) {
                        trfaccReBalance = (double) trfList.get(1);
                        trfName = (String) trfList.get(2);
                        while (!rrfpassedTest) {
                            System.out.print(
                                    "\nEnter Account No. to transfer to: #");
                            accRrfNo = sc.next();
                            rrfList = CustomerStat.retrieveAcc(accRrfNo, pin, "***");
                            rrfpassedTest = (boolean) rrfList.get(0);

                            if (accRrfNo.equals(accTrfNo)) {
                                System.out.println(
                                        ANSI_RED + "You cannot transfer to the same account.\n" + ANSI_RESET);
                                checker = false;
                            }
                        }
                        rrfaccReBalance = (double) rrfList.get(1);
                        rrfName = (String) rrfList.get(2);

                        String actionStatementTo = "TRANSFER TO ACC NO." + accRrfNo;
                        String actionStatementFrom = "TRANSFER FROM ACC NO." + accTrfNo;

                        // Get current available balance
                        double currentBalanceToTransfer = CustomerStat.getBalanceFromAccount(accTrfNo, pin);
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
                                CustomerATM.cDeposit(sc, userId, pin, accRrfNo, accTypeName, action,
                                        actionStatementFrom,
                                        transfer_amount,
                                        rrfaccReBalance, rrfName);
                                CustomerATM
                                        .cWithdraw(sc, userId, pin, accTrfNo, accTypeName, action, actionStatementTo,
                                                transfer_amount,
                                                trfaccReBalance, trfName);
                            }
                        }
                    }
                    break;

                case "3":
                    action = "withdraw";
                    String actionStatement = "ATM WITHDRAWAL";
                    accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    List<Object> rList = CustomerStat.retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = CustomerStat.getBalanceFromAccount(accReNo, pin);
                        System.out.print("\nEnter amount to withdraw: $");
                        withdrawal_amount = sc.nextDouble();
                        while (withdrawal_amount > currentBalanceToWithdraw) {
                            System.out.println(
                                    ANSI_RED + "Your balance is insufficient, please re-enter amount.\n" + ANSI_RESET);
                            System.out.print("Enter amount to withdraw: $");
                            withdrawal_amount = sc.nextDouble();
                        }
                        checker = true;
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
                        CustomerATM
                                .cWithdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                        withdrawal_amount,
                                        accReBalance, trfName);
                    }
                    break;
                case "4":
                    savingsBalance = CustomerStat.getBalanceFromAccount(SAaccNo, pin);
                    checkingBalance = CustomerStat.getBalanceFromAccount(CAaccNo, pin);
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
                    accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    rList = CustomerStat.retrieveAcc(accReNo, pin, accTypeName);
                    passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        System.out.print("\nEnter amount to deposit: $");
                        deposit_amount = sc.nextDouble();
                        checker = true;
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
                        CustomerATM
                                .cDeposit(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                        deposit_amount,
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
                CustomerATM.changePin(sc, userId);
                break;

            case "2":
                System.out.print("Chose Option 2, WIP");
                break;

            case "3":
                // when approved, set repayment date by 1 month from approval
                String action = "repay loan";
                String actionStatement = "LOAN REPAYMENT";
                List<String> accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, action);
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
                    accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    double currentMonthDebt = currentDebt / duration;

                    List<Object> payList = CustomerStat.retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) payList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) payList.get(1);
                        String trfName = (String) payList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = CustomerStat.getBalanceFromAccount(accReNo, pin);
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

                            CustomerATM
                                    .cWithdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
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
                accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
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

                CustomerATM
                        .applyLoan(sc, userId, pin, accLoanNo, accLoanTypeName, action, actionStatement, loan_amount,
                                intRate, proRate, lateRate);
                break;

            case "5":
                actionStatement = "PRINT LATEST TRANSACTIONS";
                accList = CustomerStat.selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
                String accStatNo = accList.get(0);
                String accStatType = accList.get(1);
                List<Object> pList = CustomerStat.retrieveAcc(accStatNo, pin, accStatType);
                boolean passedTest = (boolean) pList.get(0);
                if (passedTest) {
                    CustomerATM.printLatestTransactions(accStatNo, pin, accStatType);
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
                System.out.printf("Your current balance in your %s account is $%.2f\n",
                        ANSI_YELLOW + accountType + ANSI_RESET, balanceAmt);
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

    public static void deposit(String accNum, String pin, double amount, double balance)
            throws ClassNotFoundException, SQLException {
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
                    System.out.printf("You have successfully deposited $%.2f. \nYour current balance account is: $%.2f",
                            amount, balanceAmt);
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
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM customer c JOIN account a ON c.user_id = a.user_id JOIN ( SELECT * FROM transaction WHERE account_number = "
                            + accNum
                            + " ORDER BY date DESC LIMIT 1) t ON a.account_number = t.account_number WHERE a.account_number = "
                            + accNum + " AND a.pin = " + pin);
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

    public static void withdraw(String accNum, String pin, double amount, double balance)
            throws ClassNotFoundException, SQLException {
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
                    System.out.printf(
                            "You have successfully withdrawed $%.2f. \nYour current account balance is: $%.2f.\n",
                            amount, balanceAmt);
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
                "    |           | /  __  \\         /       ||  | |           |   /   \\  |           ||   \\/   | \n"
                +
                "    `---|  |----`|  |  |  |       |   (----`|  | `---|  |----`  /  ^  \\ `---|  |----`|  \\  /  | \n" +
                "        |  |     |  |  |  |        \\   \\    |  |     |  |      /  /_\\  \\    |  |     |  |\\/|  | \n"
                +
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
            Teller.TellerMode();
        } else if (mode == 2) {
            CustomerMode();
        }
    }
}
