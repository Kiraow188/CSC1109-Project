package com.sitatm.sitatm.CLI;

import com.sitatm.sitatm.Database;
import com.sitatm.sitatm.Email;
import com.sitatm.sitatm.PinHash;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

class CustomerATM extends MainAtmCli {
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