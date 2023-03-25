package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePinController {
    ATM atm = new ATM();
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account account = holder.getAccount();
    private final String fxmlFile = "atm-change-pin-view.fxml";
    @FXML
    private TextField oldPassTxtbox;
    @FXML
    private TextField newPassTxtbox;
    @FXML
    private void backAction(ActionEvent event){
        try {
            atm.changeScene("atm-main-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void exitAction(ActionEvent event){
        try {
            atm.changeScene("atm-exit-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void changePin() throws NoSuchAlgorithmException, SQLException {
        String oldPin = oldPassTxtbox.getText();
        String newPin = newPassTxtbox.getText();
        System.out.println("Debuggy{45}: Getting account information from account object");
        System.out.println("Current Account Number: "+account.getAccountNo());
        System.out.println("Current Salt: "+account.getSalt());
        System.out.println("Current Hashed Pin: "+account.getPin());

        if (oldPassTxtbox.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Change Pin Error");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter your current pin");
            withdrawConfirmation.showAndWait();
        }
        else if (newPassTxtbox.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Change Pin Error");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter your new pin");
            withdrawConfirmation.showAndWait();
        }
        else if (!newPin.matches("\\d{6}")) {
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Change Pin Error");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("New pin can only contain digits and have a length of 6");
            withdrawConfirmation.showAndWait();
        }
        else{
            boolean doesPinMatch = PinHash.hashMatching(oldPin, account.getSalt(), account.getPin());
            if (doesPinMatch) {
                System.out.println("Debuggy{44}: Password match MUTAFUKA!!!!!!!!");
                //Hash customer's new pin with Salt and Pepper
                String[] hashAlgo = PinHash.hashPin(newPin);
                String salt = hashAlgo[0];
                String hashedPin = hashAlgo[1];
                //Debug
                System.out.println("Hashed Password: " + hashAlgo[1]);
                System.out.println("Random Salt: " + hashAlgo[0]);

                String query = ("UPDATE account SET pin = ?, salt = ? WHERE account_number = ?");
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, hashedPin);
                pStatement.setString(2, salt);
                pStatement.setString(3, account.getAccountNo());
                int rowsAffected = pStatement.executeUpdate();
                if (rowsAffected > 0) {
                    Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                    succAlert.setTitle("Success!");
                    succAlert.setHeaderText(null);
                    succAlert.setContentText("Your pin has been successfully changed!");
                    succAlert.showAndWait();
                    try {
                        atm.changeScene("atm-main-view.fxml",l.getLocale());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
                withdrawConfirmation.setTitle("SIT ATM: Change Pin Error");
                withdrawConfirmation.setGraphic(null);
                withdrawConfirmation.setHeaderText("Current pin does not match!");
                withdrawConfirmation.showAndWait();
                oldPassTxtbox.clear();
                newPassTxtbox.clear();
            }
        }

    }
    public void setZN() throws IOException{
        l.setLocale(fxmlFile,"zh");
        holder.setLocalization(l);
    }
    public void setEN() throws IOException{
        l.setLocale(fxmlFile,"en");
        holder.setLocalization(l);
    }
    public void setMS() throws IOException{
        l.setLocale(fxmlFile, "ms");
        holder.setLocalization(l);
    }

}
