package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class ChangePinController {
    ATM atm = new ATM();
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private final String fxmlFile = "atm-change-pin-view.fxml";
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
