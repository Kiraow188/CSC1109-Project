package com.sitatm.sitatm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CardSelectionController {
    @FXML
    private ImageView blackCard;
    @FXML
    private ImageView purpleCard;
    @FXML
    private ImageView pinkCard;
    private void setImageSize(ImageView img){
        img.setFitWidth(304);
        img.setFitHeight(481);
    }
    @FXML
    private void initialize(){
        Image imgBlackCard = new Image(CardSelectionController.class.getResourceAsStream("/blackCard.png"));
        Image imgPurpleCard = new Image(CardSelectionController.class.getResourceAsStream("/purpleCard.png"));
        Image imgPinkCard = new Image(CardSelectionController.class.getResourceAsStream(("/pinkCard.png")));
        // Programmatically setting width and height for the ImageView
        setImageSize(blackCard);
        setImageSize(purpleCard);
        setImageSize(pinkCard);
        // Programmatically loading the image onto ImageView
        blackCard.setImage(imgBlackCard);
        purpleCard.setImage(imgPurpleCard);
        pinkCard.setImage(imgPinkCard);
        // Change image when the card reader is clicked
    }

}
