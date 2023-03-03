package com.sitatm.sitatm;

import java.security.SecureRandom;

public class BnkAccNumGen {
    private static SecureRandom secureRandom = new SecureRandom();

    public static String generateNumber() {
        int number = secureRandom.nextInt(900000) + 100000; // Generate a random number between 100,000 and 999,999
        return "250" + Integer.toString(number);
    }
}
