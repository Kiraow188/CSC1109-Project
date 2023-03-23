package com.sitatm.sitatm;

import java.util.regex.Pattern;

public class Customer {
    private String userID;
    private String fName;
    private String nric;
    private String pnumber;
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{2}\\d{7}$");
    private String country;
    private String gender;
    private String dob;
    private String mNumber;
    private static final Pattern MNUMBER_PATTERN = Pattern.compile("[6|8-9]\\d{7}");
    private String address;
    private String email;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+]+@[A-Z0-9.]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);

    public Customer() {

    }
    public String getUserID() { return userID; }
    public void setUserID(String id) { this.userID = userID; }
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
            this.nric = nric;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static boolean isValidNRIC(String nric) {
        if (nric.length() != 9) {
            return false;
        }

        char firstChar = nric.charAt(0);

        if (firstChar != 'S' && firstChar != 'T' && firstChar != 'F' && firstChar != 'G') {
            return false;
        }

        int[] multipliers = {2, 7, 6, 5, 4, 3, 2};
        int total = 0;

        for (int i = 1; i < 8; i++) {
            int digit = Character.digit(nric.charAt(i), 10);
            total += digit * multipliers[i - 1];
        }

        if (firstChar == 'T' || firstChar == 'G') {
            total += 4;
        }

        int remainder = total % 11;
        char[] lastChars = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};

        if (firstChar == 'S' || firstChar == 'T') {
            return nric.charAt(8) == lastChars[remainder];
        } else {
            return nric.charAt(8) == lastChars[remainder + 4];
        }
    }
    public static boolean isValidPassport(String passportNumber) {
        return PASSPORT_PATTERN.matcher(passportNumber).matches();
    }
    public static boolean isValidMobileNumber(String mnum){
        return MNUMBER_PATTERN.matcher(mnum).matches();
    }
    public static boolean isValidEmail(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }
}