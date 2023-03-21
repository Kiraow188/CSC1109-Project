package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CashDepositController {
    ATM atm = new ATM();
    @FXML
    private void backAction(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("atm-main-view.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();
            controller.setWelcomeMsg();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            //atm.changeScene("atm-main-view.fxml");
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
}
