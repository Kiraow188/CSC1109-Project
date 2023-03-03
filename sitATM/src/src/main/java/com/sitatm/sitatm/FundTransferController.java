package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class FundTransferController {
    ATM atm = new ATM();

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
}
