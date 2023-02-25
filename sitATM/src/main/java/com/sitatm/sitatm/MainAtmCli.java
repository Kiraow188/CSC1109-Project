package com.sitatm.sitatm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MainAtmCli {
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
            int totalBalance = 0;
            int count = 0;
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(2));
                firstName = resultSet.getString(2);
                totalBalance = resultSet.getInt(5);
                count++;
            }
            int choice;
            int deposit_amount;
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
                            System.out.println("Enter amount to withdraw");
                            withdrawal_amount = sc.nextInt();
                            if (withdrawal_amount > totalBalance) {
                                System.out.println("Your balance is insufficient");
                            } else {
                                totalBalance = totalBalance - withdrawal_amount;
                                int withdrawAmountBalance = statement.executeUpdate("UPDATE customer SET totalBalance = " + totalBalance + " WHERE pinNumber =" + pin);
                                System.out.println("You have are successfully withdraw $" + withdrawal_amount + " and your current balance is $" + totalBalance);
                            }
                            break;
                        case 2:
                            System.out.println("Enter amount to transfer");
                            break;
                        case 3:
                            System.out.println("Your current Balance is $" + totalBalance);
                            break;
                        case 4:
                            System.out.println("Here are your list of accounts");
                            break;
                        case 5:
                            System.out.println("Enter amount to deposit");
                            // input from customer
                            deposit_amount = sc.nextInt();
                            totalBalance = totalBalance + deposit_amount;
                            int depositAmountBalance = statement.executeUpdate("UPDATE customer SET totalBalance = " + totalBalance + " WHERE pinNumber =" + pin);
                            System.out.println("Successfully  deposit $" + deposit_amount + " and your current balance is $" + totalBalance);
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
