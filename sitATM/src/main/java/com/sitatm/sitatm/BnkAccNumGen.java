package com.sitatm.sitatm;

import java.security.SecureRandom;

public class BnkAccNumGen {
    private static SecureRandom secureRandom = new SecureRandom();

    public static String generateNumber(int accType) {
        int number = secureRandom.nextInt(900000) + 100000; // Generate a random number between 100,000 and 999,999
        if (accType == 1){
            //Savings Account Prefix
            return "250" + Integer.toString(number);
        }else{
            //Current Account Prefix
            return "360" + Integer.toString(number);
        }

    }
}
