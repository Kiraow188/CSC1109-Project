package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ExitViewController {
    ATM atm = new ATM();
    ATMController atmc = new ATMController();
    @FXML
    private ImageView cardReader;
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private final String fxmlFile = "atm-exit-view.fxml";
    @FXML
    private Image collectCardImage = atmc.updateImage("/img/card_reader_collect_card.png");
    @FXML
    private Image cardReaderWCardImg = atmc.updateImage("/img/card_reader_w_card.png");

    public void collectCardImg(){
        atmc.setImageSize(cardReader,428,512);
        cardReader.setImage(collectCardImage);
    }

    public void returnToLogin(){
        try {
            atm.changeScene("atm-login-view.fxml");
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

    @FXML
    private void initialize() {
        atmc.setImageSize(cardReader,428,369);
        cardReader.setImage(cardReaderWCardImg);
    }
}
