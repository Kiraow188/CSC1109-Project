package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PinViewController {
    ATM atm = new ATM();
    @FXML
    private PasswordField pinTextBox;
    @FXML
    private Button cancelButton;

    @FXML
    private void keypadButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonValue = button.getText();
        // debugging
        System.out.println(buttonValue);
        String currentPassword = pinTextBox.getText();
        pinTextBox.setText(currentPassword + buttonValue);
    }

    @FXML
    private void clearButtonAction(ActionEvent event){
        pinTextBox.clear();
    }

    @FXML
    private void cancelButtonAction(ActionEvent event){
        // TODO: either close this stage or close the program
        try {
            atm.changeScene("atm-login-view.fxml");
            //((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void confirmButtonAction(ActionEvent event){
        // TODO: check pin then move to the next stage if not, clear textbox and prompt for retry
        try {
            atm.changeScene("atm-main-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /**If pin is incorrect prompt:
        Alert wrongPin = new Alert(Alert.AlertType.ERROR);
        wrongPin.setTitle("Incorrect PIN");
        wrongPin.setHeaderText(null);
        wrongPin.setContentText("The PIN you entered is incorrect. Please try again.");
        wrongPin.showAndWait();**/
    }
}
