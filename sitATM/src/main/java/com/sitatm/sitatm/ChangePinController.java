package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangePinController {
    ATM atm = new ATM();
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account account = holder.getAccount();
    private final String fxmlFile = "atm-change-pin-view.fxml";
    @FXML
    private TextField oldPassTxtbox;
    @FXML
    private TextField newPassTxtbox;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnConfirm;
    @FXML
    private void backAction(ActionEvent event){
        try {
            atm.changeScene("atm-more-services-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void exitAction(ActionEvent event){
        try {
            atm.changeScene("atm-exit-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void changePin() throws NoSuchAlgorithmException, SQLException {
        String oldPin = oldPassTxtbox.getText();
        String newPin = newPassTxtbox.getText();

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
                //Hash customer's new pin with Salt and Pepper
                String[] hashAlgo = PinHash.hashPin(newPin);
                String salt = hashAlgo[0];
                String hashedPin = hashAlgo[1];

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
    @FXML
    public void initialize(){
        // Set the style of the button when the mouse is over it
        btnConfirm.setOnMouseEntered(e -> btnConfirm.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 24px;"));
        // Set the style of the button when the mouse leaves it
        btnConfirm.setOnMouseExited(e -> btnConfirm.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 24px;"));

        URL imageUrlExit = getClass().getResource("/img/exit.png");
        Image exitImage = new Image(imageUrlExit.toString());
        ImageView exitImgView = new ImageView(exitImage);
        URL imageUrlBack = getClass().getResource("/img/left-arrow.png");
        Image backImage = new Image(imageUrlBack.toString());
        ImageView backImgView = new ImageView(backImage);

        btnExit.setGraphic(exitImgView);
        btnExit.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-color: transparent;");
        btnBack.setGraphic(backImgView);
        btnBack.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-color: transparent;");
    }

}
