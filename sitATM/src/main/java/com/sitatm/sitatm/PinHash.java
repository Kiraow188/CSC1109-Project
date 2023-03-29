package com.sitatm.sitatm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PinHash {
    private static final String PEPPER = "S9I8T7A6T5M4S3A2L1T0BA6E9";

    public static String[] hashPin(String password) throws NoSuchAlgorithmException{
        String rSalt = generateSalt();
        String saltedPepperPassword = rSalt + password + PEPPER;
        String hashedPassword = hashString(saltedPepperPassword);
        String[] HashNSalt = new String[2];
        HashNSalt[0] = rSalt;
        HashNSalt[1] = hashedPassword;
        return HashNSalt;
    }

    private static String hashString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(string.getBytes());
        String encodedHash = Base64.getEncoder().encodeToString(hash);
        encodedHash = encodedHash.replaceAll("/", "a");
        encodedHash = encodedHash.replaceAll("\\\\", "b");
        return encodedHash;
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        // Replace any '/' and '\' characters with a valid replacement
        encodedSalt = encodedSalt.replaceAll("/", "a");
        encodedSalt = encodedSalt.replaceAll("\\\\", "b");
        return encodedSalt;
    }

    public static boolean hashMatching(String pin, String salt, String hp) throws NoSuchAlgorithmException{
        String saltedPepperPassword = salt + pin + PEPPER;
        String hashedPassword = hashString(saltedPepperPassword);
        if (hashedPassword.equals(hp)){
            return true;
        }
        return false;
    }
}
