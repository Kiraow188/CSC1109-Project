package com.sitatm.sitatm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PinHash {
    private static final String PEPPER = "S9I8T7A6T5M4S3A2L1T0BA6E9";

    public static String[] hashPin(String password) throws NoSuchAlgorithmException{
        // Generate a random salt
        String rSalt = generateSalt();
        // Combine salt + password + pepper
        String saltedPepperPassword = rSalt + password + PEPPER;
        // Hash the combined String using the helper function hashString
        String hashedPassword = hashString(saltedPepperPassword);
        // Store the salt and the hashed password into an array
        String[] HashNSalt = new String[2];
        HashNSalt[0] = rSalt;
        HashNSalt[1] = hashedPassword;
        // Return the array
        return HashNSalt;
    }

    // Helper method to hash a string using SHA-256
    private static String hashString(String string) throws NoSuchAlgorithmException {
        // Creates a MessageDigest object with SHA-256 algorithm
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // Generates a hash of the input string
        byte[] hash = digest.digest(string.getBytes());
        // Encode the above hash into a Base64 string
        String encodedHash = Base64.getEncoder().encodeToString(hash);
        // Replace any / or \ characters with a valid replacement
        encodedHash = encodedHash.replaceAll("/", "a");
        encodedHash = encodedHash.replaceAll("\\\\", "b");
        return encodedHash;
    }

    // Helper method to generate a random salt
    private static String generateSalt() {
        // Creates a secure random object
        SecureRandom random = new SecureRandom();
        // Generate 16 random bytes for the salt
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        // Encode the salt to a base64 String
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        // Replace any '/' and '\' characters with a valid replacement
        encodedSalt = encodedSalt.replaceAll("/", "a");
        encodedSalt = encodedSalt.replaceAll("\\\\", "b");
        return encodedSalt;
    }

    public static boolean hashMatching(String pin, String salt, String hp) throws NoSuchAlgorithmException{
        // Combine salt, pin and pepper
        String saltedPepperPassword = salt + pin + PEPPER;
        // Hash the combined string using SHA-256
        String hashedPassword = hashString(saltedPepperPassword);
        // Check if the hashed massword matches the provided hash
        if (hashedPassword.equals(hp)){
            // Return true if they match
            return true;
        }
        // Return false if they don't
        return false;
    }
}
