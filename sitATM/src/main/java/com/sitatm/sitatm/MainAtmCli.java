package com.sitatm.sitatm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MainAtmCli {

    private final static int checking =1;
	private final static int savings =2;
    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/sitatm";
        String user = "root";
        String pass = "";


        try {
            // SQL Connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pass);
            java.sql.Statement statement = connection.createStatement();
            Scanner sc = new Scanner(System.in);
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
            System.out.println("Please Enter Your Pin");
            int pin = sc.nextInt();
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
                            while (persist)
                            {
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
                            if (accType == checking)
                            {
                                while (withdrawal_amount > checkingBalance) {
                                    System.out.println("Your balance is insufficient, please re-enter amount");
                                    withdrawal_amount = sc.nextInt();
                                } 
                                checkingBalance = checkingBalance - withdrawal_amount;
                                int withdrawAmountBalance = statement.executeUpdate("UPDATE customer SET aCheckingBalance = " + checkingBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have are successfully withdraw $" + withdrawal_amount + " and your current balance is $" + checkingBalance);
                            
                            }
                            else 
                            {
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
                            }else {
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
                            while (persistent)
                            {
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
                            if (accType == checking)
                            {
                                checkingBalance = checkingBalance + deposit_amount;
                                int depositAmountBalance = statement.executeUpdate("UPDATE customer SET aCheckingBalance = " + checkingBalance + " WHERE aPasscode =" + pin);
                                System.out.println("You have successfully deposited $" + deposit_amount + " and your current balance is $" + checkingBalance);
                            
                            }
                            else 
                            {
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
}
