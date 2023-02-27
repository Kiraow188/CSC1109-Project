package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CashWithdrawalController {
    ATM atm = new ATM();
    //ConfirmationViewController cvc = new ConfirmationViewController();
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnConfirm;
    @FXML
    private TextField txtFieldAmt;
    @FXML
    private Button btnBackspace;

    @FXML
    private void backAction(ActionEvent event){
        try {
            atm.changeScene("atm-main-view.fxml");
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
    private void keypadButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonValue = button.getText();
        // debugging
        System.out.println(buttonValue);
        String currentPassword = txtFieldAmt.getText();
        txtFieldAmt.setText(currentPassword + buttonValue);
    }
    @FXML
    private void handleBackspaceButton(ActionEvent event) {
        int length = txtFieldAmt.getLength();
        if (length > 0) {
            txtFieldAmt.deleteText(length - 1, length);
        }
    }

    @FXML
    private void withdraw(ActionEvent event){
        /**
        Alert withdrawConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
        withdrawConfirmation.setGraphic(null);
        withdrawConfirmation.setHeaderText("Are you sure you want to withdraw $"+txtFieldAmt.getText()+" from your account?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        withdrawConfirmation.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = withdrawConfirmation.showAndWait();
        if (result.get() == yesButton){
            // Call your "Yes" function here
        } else {
            // Call your "No" function here
        }**/
        if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else if (Integer.parseInt(txtFieldAmt.getText()) < 20){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Minimum withdrawal amount is $20!");
            withdrawConfirmation.showAndWait();
        }
        else {
            try {
                String amount = txtFieldAmt.getText();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("atm-confirmation-view.fxml"));
                Parent root = loader.load();
                ConfirmationViewController cvc = loader.getController();
                cvc.cwConfirmation(amount);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
