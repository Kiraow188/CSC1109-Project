package com.sitatm.sitatm.CLI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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