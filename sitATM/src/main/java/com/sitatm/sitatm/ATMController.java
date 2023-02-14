package com.sitatm.sitatm;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ATMController {
    @FXML
    private ImageView cardReader;
    @FXML
    private ImageView receiptPrinter;

    @FXML
    private void initialize(){
        Image insertCard = new Image(ATMController.class.getResourceAsStream("/insertCard.png"));
        Image printer = new Image(ATMController.class.getResourceAsStream("/atmReceipt.png"));
        Image cardInserted = new Image(ATMController.class.getResourceAsStream(("/cardInsert.png")));
        // Programmatically setting width and height for the ImageView
        cardReader.setFitWidth(200);
        cardReader.setFitHeight(350);
        receiptPrinter.setFitWidth(200);
        receiptPrinter.setFitHeight(350);
        // Programmatically loading the image onto ImageView
        cardReader.setImage(insertCard);
        receiptPrinter.setImage(printer);
        // Change image when the card reader is clicked
        cardReader.setOnMouseClicked(event -> {
            cardReader.setImage(cardInserted);
        });
    }
}