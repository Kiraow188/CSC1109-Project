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
        //return "Random Salt: "+ rSalt + " | Hashed Password: " + hashedPassword;
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
        //return Base64.getEncoder().encodeToString(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        // Replace any '/' and '\' characters with a valid replacement
        encodedSalt = encodedSalt.replaceAll("/", "a");
        encodedSalt = encodedSalt.replaceAll("\\\\", "b");
        return encodedSalt;
    }

    public static boolean hashMatching(String pin, String salt, String hp) throws NoSuchAlgorithmException{
        String saltedPepperPassword = salt + pin + PEPPER;
        System.out.println("Salt + Password + Pin: "+saltedPepperPassword);
        String hashedPassword = hashString(saltedPepperPassword);
        System.out.println("Hashed Password: "+hashedPassword);
        System.out.println("Stored Hash Password: "+hp);
        System.out.println("Length of Hashed Pin: "+hashedPassword.length());
        System.out.println("Length of Stored Pin: "+hp.length());
        if (hashedPassword.equals(hp)){
            System.out.println("Password Match! Access granted");
            return true;
        }
        System.out.println("Password does not match! Access denied");
        return false;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String password = "123456";
        String[] pinNSalt = hashPin(password);
        System.out.println("Random Salt: " + pinNSalt[0]);
        System.out.println("Hashed Password: " + pinNSalt[1]);
    }

}
