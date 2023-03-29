package com.sitatm.sitatm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.Transport;

public class Email {
    // email ID of Sender.
    private static final String SENDER_EMAIL = "hello.sitatm@outlook.com";
    // password of Sender's email account.
    private static final String SENDER_PASSWORD = "4ky7PswJ1DR9vAZo2kpp";
    // host name for Outlook's SMTP server
    private static final String HOST = "smtp.office365.com";
    // port number for SSL/TLS
    private static final int PORT = 587;

    public static void sendEmailPrep(String accNum,int emailType) throws SQLException {
        Database db = new Database();
        //emailType = 1 is [Welcome email for account creation]
        if (emailType == 1){
            try{
                ResultSet resultSet = db.executeQuery("SELECT customer.full_name, customer.email, account.account_number, account.account_type\n" +
                        "FROM customer\n" +
                        "INNER JOIN account ON account.user_id = customer.user_id\n" +
                        "WHERE account.account_number ='"+accNum+"'");
                if (resultSet.next()){
                    String fName = resultSet.getString("full_name");
                    String email = resultSet.getString("email");
                    String accType = resultSet.getString("account_type");
                    sendWelcomeEmail(fName,email,accNum,accType);
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
        //emailType = 2 is [Card Creation Email]
        else if (emailType == 2) {
            java.util.Date dt = new java.util.Date();
            SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
            String creationDate = cd.format(dt);
            try{
                ResultSet resultSet = db.executeQuery("SELECT customer.full_name, customer.email, account.account_number, card.card_number, card.card_type, card.cvv, card.expiry_date\n" +
                        "FROM customer\n" +
                        "INNER JOIN account ON account.user_id = customer.user_id\n" +
                        "INNER JOIN card ON card.account_number = account.account_number\n" +
                        "WHERE account.account_number ='"+accNum+"' AND card.created_date ='"+creationDate+"'");
                if (resultSet.next()){
                    String fName = resultSet.getString("full_name");
                    String email = resultSet.getString("email");
                    String cardNum = resultSet.getString("card_number");
                    String cardType = resultSet.getString("card_type");
                    String cvv = resultSet.getString("cvv");
                    String expDate = resultSet.getString("expiry_date");
                    sendCardCreation(fName,email,accNum,cardNum,cardType,cvv,expDate);
                }
            } catch (SQLException e) {
                System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
            }
        }
    }

    public static void sendWelcomeEmail(String fName, String recipient, String accNum, String accType) {
        try {
            Session session = createSession();
            String messageText = "Dear " + fName + ",\n\n" +
                    "Welcome to SITBank!\n\n" +
                    "Your account has been successfully created.\n\n" +
                    "Here is a summary of your account information.\n" +
                    "----------------------------------------------------------------------\n" +
                    "Account number: " + accNum + "\n" +
                    "Account Type: " + accType + "\n\n" +
                    "Thank you for choosing our bank.\n\n" +
                    "Best regards,\n" +
                    "The SIT Bank Team";
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            // Set Subject: subject of the email
            message.setSubject("Welcome to SIT Bank");
            // set body of the email.
            message.setText(messageText);
            // Send email.
            Transport.send(message);
            System.out.println("Welcome email is successfully sent to " + recipient);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void sendCardCreation(String fName, String recipient, String accNum, String cardNum, String cardType, String cvv, String expDate) {
        try {
            Session session = createSession();
            String messageText = "Dear " + fName + ",\n\n" +
                    "Thank you for creating your bank card with us!\n\n" +
                    "Here is a summary of your card information.\n" +
                    "----------------------------------------------------------------------\n" +
                    "Card number: " + cardNum + "\n" +
                    "Card type: " + cardType + "\n" +
                    "CVV Number: " + cvv + "\n" +
                    "Card Expiry Date: " + expDate + "\n" +
                    "Account number card is paired to: " + accNum + "\n\n" +
                    "Thank you for banking with us.\n\n" +
                    "Best regards,\n" +
                    "The SIT Bank Team";
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            // Set Subject: subject of the email
            message.setSubject("Welcome to SIT Bank");
            // set body of the email.
            message.setText(messageText);
            // Send email.
            Transport.send(message);
            System.out.println("Card application is successfully sent to " + recipient);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void sendLoanApplicationEmail(String fName, String recipient) {
        try {
            Session session = createSession();

            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            // Set Subject: subject of the email
            message.setSubject("Your Loan Application");
            // set body of the email.
            message.setText("Dear "+fName+",\n\nThank you for applying for a loan with SITATM. We are reviewing your application and will get back to you shortly.\n\nThank you,\nThe SITATM Team");
            // Send email.
            Transport.send(message);
            System.out.println("Loan application email successfully sent to " + recipient);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public static void sendLoanApprovalEmail(String fName, String recipient, double principle, String intRate, int duration, double repayment) {
        try {
            Session session = createSession();
            String messageText = "Dear " + fName + ",\n\n" +
                    "Your loan application has been approved!\n\n" +
                    "Here is breakdown of your loan application\n" +
                    "----------------------------------------------------------------------\n" +
                    "Principle: $" + principle + "\n" +
                    "Interest Rate: " + intRate + "%\n" +
                    "Monthly Repayment Amount: $" + repayment + "\n" +
                    "Term: " + duration + " Months \n\n" +
                    "Thank you for banking with us.\n\n" +
                    "Best regards,\n" +
                    "The SIT Bank Team";
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            // Set Subject: subject of the email
            message.setSubject("SIT Bank Loan Application Result");
            // set body of the email.
            message.setText(messageText);
            // Send email.
            Transport.send(message);
            System.out.println("Loan application email successfully sent to " + recipient);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public static void sendLoanRejectionEmail(String fName, String recipient) {
        try {
            Session session = createSession();
            String messageText = "Dear " + fName + ",\n\n" +
                    "We regret to inform you that your loan application has been rejected.\n\n" +
                    "Thank you for banking with us.\n\n" +
                    "Best regards,\n" +
                    "The SIT Bank Team";
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            // Set Subject: subject of the email
            message.setSubject("SIT Bank Loan Application Result");
            // set body of the email.
            message.setText(messageText);
            // Send email.
            Transport.send(message);
            System.out.println("Loan application email successfully sent to " + recipient);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static Session createSession() {
        // Getting system properties
        Properties properties = System.getProperties();
        // Setting up mail server
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        // creating session object to get properties
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }
}
