package com.sitatm.sitatm;

import java.security.SecureRandom;

public class CVVGenerator {
    private static SecureRandom secureRandom = new SecureRandom();

    public static String generateCVV(String cardNumber, String expiryDate) {
        // Take the last 3 digits of the card number
        String lastThreeDigits = cardNumber.substring(cardNumber.length() - 3);

        // Take the last 2 digits of the expiry year
        int expiryYearLastTwoDigits = Integer.parseInt(expiryDate.substring(3));
        int expiryMonth = Integer.parseInt(expiryDate.substring(0, 2));

        // Combine the above values to generate the CVV code
        int cvv = secureRandom.nextInt(10) * 100 + secureRandom.nextInt(10) * 10
                + expiryYearLastTwoDigits + expiryMonth + Integer.parseInt(lastThreeDigits);
        // Add zero padding in front of the CVV if it's 2 digit
        String formattedCVV = String.format("%03d", cvv % 1000);

        // Return the formatted CVV
        return formattedCVV;
    }
    /* Debug
    public static void main(String[] args) {
        String cardNumber = "1234567890123456";
        String cvv = CVVGenerator.generateCVV(cardNumber, "05/2024");
        System.out.println("CVV code: " + cvv);
    }
     */
}
