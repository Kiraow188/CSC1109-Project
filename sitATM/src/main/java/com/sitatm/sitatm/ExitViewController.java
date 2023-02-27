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

    @FXML
    private void initialize() {
        atmc.setImageSize(cardReader,428,369);
        cardReader.setImage(cardReaderWCardImg);
    }
}
