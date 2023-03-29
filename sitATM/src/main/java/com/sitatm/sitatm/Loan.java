package com.sitatm.sitatm;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Loan {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    static final private double maximumLoanAmount = 100000.0;

    public static boolean approveLoan(double requestedAmount){
        return !(requestedAmount > maximumLoanAmount);
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

                ResultSet getInfo = db.executeQuery(
                        "SELECT customer.full_name,customer.email FROM customer INNER JOIN account ON customer.user_id = account.user_id WHERE account.account_number = '"
                                + accNum + "'");
                while (getInfo.next()) {
                    fName = getInfo.getString("full_name");
                    email = getInfo.getString("email");
                }
                ResultSet approvalResultSet = db.executeQuery(
                        "SELECT * FROM loan WHERE status = 'PENDING' and account_number = '" + accNum + "'");
                if (approvalResultSet.next()) {
                    requestedAmount = Double.parseDouble(approvalResultSet.getString("principle_amt"));
                    String intRate = approvalResultSet.getString("interest_rate");
                    int duration = Integer.parseInt(approvalResultSet.getString("duration"));
                    double debt = Double.parseDouble(approvalResultSet.getString("debt"));
                    double repayment = debt / duration;
                    loanID = approvalResultSet.getInt("loan_id");
                    String status;
                    if (!approveLoan(requestedAmount)) {
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
                                Email.sendLoanRejectionEmail(fName, email);
                                Teller.showTellerMenu();
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL Exception caught: " + e);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println("No Such Algorithm Exception caught: " + e);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Runtime Exception caught: " + e);
                        }
                    } else {
                        status = "APPROVED";
                        // Get today's date
                        LocalDate today = LocalDate.now();
                        // Add one month to today's date
                        LocalDate repaymentDate = today.plusMonths(1);
                        try {
                            String query = "UPDATE loan SET status = ?, repayment_date = ? WHERE account_number = ? AND loan_id = ?";
                            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                            pStatement.setString(1, status);
                            pStatement.setDate(2, java.sql.Date.valueOf(repaymentDate));
                            pStatement.setString(3, accNum);
                            pStatement.setInt(4, loanID);
                            int rowsAffected = pStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("The loan has been approved!\n");
                                Email.sendLoanApprovalEmail(fName, email, requestedAmount, intRate, duration,
                                        repayment);
                                Teller.showTellerMenu();
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL Exception caught: " + e);
                        } catch (NoSuchAlgorithmException e) {
                            System.out.println("No Such Algorithm Exception caught: " + e);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Runtime Exception caught: " + e);
                        }
                    }
                }
            } else {
                System.out.println("\nNo loans to be approved, Looks like it's a quite day huh...\n");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception caught: " + e);
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
        List<Object> rList = Account.retrieveAcc(accReNo, pin, accTypeName);
        boolean passedTest = (boolean) rList.get(0);
        if (passedTest) {
            try {
                int count = 0;
                ResultSet checkLoanIP = db.executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                        + accReNo + " AND loan.status = 'PENDING'"); //
                while (checkLoanIP.next()) {
                    count++;
                }
                checkLoanIP = db.executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                        + accReNo + " AND loan.status = 'APPROVED'"); //
                while (checkLoanIP.next()) {
                    count++;
                }

                if (count > 0) {
                    System.out.printf("\n%s%s account has an ongoing loan.%s\n\n",
                            Customer.ANSI_RED, accTypeName, Customer.ANSI_RESET);
                } else {
                    String loanTypeOp;
                    int loanType = 0;
                    boolean persistent = true;
                    while (persistent) {
                        persistent = false;
                        System.out.println(
                                Customer.ANSI_CYAN + "\nPlease select Loan Tenor: " + Customer.ANSI_RESET
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
                                        Customer.ANSI_RED + "Invalid option, "
                                                + "please re-enter.\n" + Customer.ANSI_RESET);
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
                                Customer.ANSI_CYAN + "Proceed with loan request?  " + Customer.ANSI_RESET
                                        + "\n[1] Yes\n[2] No" + Customer.ANSI_RESET);
                        String loanOp = sc.next();
                        persistent = false;
                        switch (loanOp) {
                            case "1":
                                try {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    calcDebt = Double.valueOf(df.format(calcDebt));

                                    String depositAmountBalanceQuery = "INSERT INTO `loan`(`loan_id`, `account_number`, `principle_amt`, `interest_rate`, `duration`, `debt`, `date_created`, `repayment_date`, `status`) VALUES (?,?,?,?,?,?,?,?,?);";
                                    PreparedStatement depositAmountBalance = db.getConnection().prepareStatement(depositAmountBalanceQuery);
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
                                                Customer.ANSI_RED, Customer.ANSI_RESET);
                                        break;
                                    } else {
                                        System.out.println(
                                                Customer.ANSI_CYAN + "\nLoan request has been processed.\n"
                                                        + Customer.ANSI_RESET);
                                        Email.sendLoanApplicationEmail(fName, email);
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println(
                                            Customer.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                    + Customer.ANSI_RESET);
                                }
                                break;
                            case "2":
                                System.out.println(
                                        Customer.ANSI_RED + "\nLoan request cancelled.\n"
                                                + Customer.ANSI_RESET);
                                break;
                            default:
                                System.out.println(
                                        Customer.ANSI_RED + "Invalid option, "
                                                + "please re-enter.\n" + Customer.ANSI_RESET);
                                persistent = true;
                        }
                    }
                }
            } catch (Exception e) {
                // System.out.println("Error: " + e.getMessage());
                System.out.println(
                        Customer.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                + Customer.ANSI_RESET);
                return;
            }
        }
    }
}
