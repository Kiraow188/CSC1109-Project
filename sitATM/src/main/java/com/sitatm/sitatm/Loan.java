package com.sitatm.sitatm;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Loan {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    static final private double maximumLoanAmount = 30000.0;

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
}
