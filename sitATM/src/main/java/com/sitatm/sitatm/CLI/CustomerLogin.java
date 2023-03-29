package com.sitatm.sitatm.CLI;

import com.sitatm.sitatm.Database;
import com.sitatm.sitatm.PinHash;

import java.sql.ResultSet;
import java.sql.SQLException;

class CustomerLogin extends MainAtmCli {
   
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