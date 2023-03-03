import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashMcSaltPepperBae {
    private static final String CONSTANTSALT = "S1I2T3A4T5M6S7A8L9T0BA1E2";
    private static final String PEPPER = "S9I8T7A6T5M4S3A2L1T0BA6E9";
    
    /**
    public static String hashPasswordCSalt(String password) throws NoSuchAlgorithmException {
        String saltedPassword = CONSTANTSALT + password;
        String hashedPassword = hashString(saltedPassword);
        return hashedPassword;
    }

    public static String hashPasswordRSalt(String password) throws NoSuchAlgorithmException{
        String rSalt = generateSalt();
        String saltedPassword = rSalt + password;
        String hashedPassword = hashString(saltedPassword);
        return "Random Salt: "+ rSalt + " | Hashed Password: " + hashedPassword;
    }
     */
    public static String hashPasswordRSaltP(String password) throws NoSuchAlgorithmException{
        String rSalt = generateSalt();
        String saltedPepperPassword = rSalt + password + PEPPER;
        String hashedPassword = hashString(saltedPepperPassword);
        return "Random Salt: "+ rSalt + " | Hashed Password: " + hashedPassword;
    }
    
    private static String hashString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(string.getBytes());
        String encodedHash = Base64.getEncoder().encodeToString(hash);
        return encodedHash;
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
      }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String password = "mySecurePassword123";
        /* 
        String constantSalt = hashPasswordCSalt(password);
        String randomSalt = hashPasswordRSalt(password); 
        System.out.println("\nHashed password using Constant Salt\n" + constantSalt);
        System.out.println("\nHashed password using Random Salt\n " + randomSalt);
        */
        
        //We'll be using this one -- store both hashed password and randomised salt to the db, store the pepper in our application
        String randomSaltPepper = hashPasswordRSaltP(password);
        System.out.println("\nHashed password using Random Salt with added pepper\n " + randomSaltPepper);
    }
}
