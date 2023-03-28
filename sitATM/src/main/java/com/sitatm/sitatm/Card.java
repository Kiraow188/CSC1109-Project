package com.sitatm.sitatm;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Card {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private String cardNumber;
    private String accountNumber;
    private String cardType;
    private String expDate;
    private String CVV;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public static void createCard(String accNum) throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        Card card = new Card();
        card.setAccountNumber(accNum);

        System.out.println("\nCard Creation Process");
        int selection;
        do {
            System.out.println("""
                \nPlease select the card type:
                [1] Debit Card
                [2] Credit Card
                """);
            while (!sc.hasNextInt()) {
                System.out.println(ANSI_RED+"""
                    \nInvaild Selection.
                    Please select the card type:
                    [1] Debit Card
                    [2] Credit Card
                    """+ANSI_RESET);
                sc.next();
            }
            selection = sc.nextInt();
        } while (selection != 1 && selection != 2);
        if (selection == 1) {
            card.setCardType("Debit Card");
        } else {
            card.setCardType("Credit Card");
        }

        int cardNetworkSelection;
        do{
            System.out.println("""
                Please select the card network:
                [1] Mastercard
                [2] Visa
                """);
            while (!sc.hasNextInt()) {
                System.out.println(ANSI_RED+"""
                    \nInvalid Selection.
                    Please select the card network:
                    [1] Mastercard
                    [2] Visa
                    """+ANSI_RESET);
                sc.next();
            }
            cardNetworkSelection = sc.nextInt();
        } while(selection != 1 && selection != 2);

        // Generate Card Number
        String cardNumber = null;
        while (cardNumber == null) {
            // Generate 16 digit card number depending on Visa or Masters
            cardNumber = CardGenerator.generateCardNumber(cardNetworkSelection);
            // Debug
            System.out.println("ATM Card Number Generated: " + cardNumber);
            try {
                ResultSet resultSet = db
                        .executeQuery("SELECT card_number,account_number FROM card WHERE card_number=" + cardNumber);
                if (resultSet.next()) {
                    System.out.println("This card number already exist!" +
                            "\nRegenerating a new card number...");
                    cardNumber = null; // Reset cardNumber to null to regenerate a new card number
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
        card.setCardNumber(cardNumber);
        // Create Expiry date (+5 years from date of issue)
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.YEAR, 5); // add 5 years
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        // String formattedExpiryDate = sdf.format(expiryDate.getTime());
        card.setExpDate(sdf.format(expiryDate.getTime()));
        // debug
        System.out.println(card.getExpDate());

        // generate CVV
        card.setCVV(CVVGenerator.generateCVV(cardNumber, card.getExpDate()));
        // debug
        System.out.println("CVV:" + card.getCVV());

        // Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = cd.format(dt);
        try {
            String query = "INSERT INTO card(card_number,account_number,card_type,expiry_date,cvv,created_date) VALUES(?,?,?,?,?,?)";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, card.getCardNumber());
            pStatement.setString(2, card.getAccountNumber());
            pStatement.setString(3, card.getCardType());
            pStatement.setString(4, card.getExpDate());
            pStatement.setString(5, card.getCVV());
            pStatement.setString(6, creationDate);
            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The customer's card is successfully created!");
                Email.sendEmailPrep(accNum, 2);
                Teller.showTellerMenu();
            } else {
                System.out.println("Something went wrong, please try again later.");
            }
            db.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void addCard() {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        System.out.println("\n[Notice] The account number entered will be the account tied to the card!");
        System.out.println("Please enter customer's current account number: ");
        String accNo;
        while (true) {
            accNo = sc.next();
            if (accNo.matches("(?=^(250|360)\\d{6}$)\\d{9}")) { // Check if input is a 9-digit number
                break; // Valid input, exit loop
            } else {
                System.out.println("Invalid input. Please enter customer's current account number: ");
            }
        }
        try {
            ResultSet resultSet;
            resultSet = db.executeQuery("SELECT * FROM account where account_number = " + accNo);
            if (resultSet.next()) {
                resultSet = db.executeQuery("SELECT account_number FROM card where account_number = " + accNo);
                if (resultSet.next()) {
                    createCard(accNo);
                }
            } else {
                int selection;
                do{
                    System.out.println(ANSI_CYAN+"""
                        \nNo customer account found!
                        Do you want to create one now?
                        [1] Yes
                        [2] No"""+ANSI_RESET);
                    while (!sc.hasNextInt()) {
                        System.out.println(ANSI_RED+"""
                        \nInvaild Selection.
                        There is no customer account found,
                        Do you want to create one now?
                        [1] Yes
                        [2] No"""+ANSI_RESET);
                        sc.next();
                    }
                    selection = sc.nextInt();
                } while(selection != 1 && selection != 2);
                if (selection == 1) {
                    Customer.CreateNewCustomerAccount();
                } else {
                    Teller.showTellerMenu();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithmic Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class Error: " + e.getMessage());
        }
    }
}
