package com.sitatm.sitatm;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ATMController {
    ATM atm = new ATM();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView cardReader;
    private Image cardReaderImg = updateImage("/img/card_reader_wo_card.png");
    private Image cardReaderWCardImg = updateImage("/img/card_reader_w_card.png");

    private void setImageSize(ImageView img){
        img.setFitWidth(428);
        img.setFitHeight(191);
    }
    public void setImageSize(ImageView img, int w, int h){
        img.setFitWidth(w);
        img.setFitHeight(h);
    }
    public Image updateImage(String imagePath) {
        return new Image(ATMController.class.getResourceAsStream(imagePath));
    }
    @FXML
    private void initialize() {
        // Programmatically setting width and height for the ImageView using setImageSize()
        setImageSize(cardReader);
        // Programmatically load the image onto the ImageView
        cardReader.setImage(cardReaderImg);
        // Change image when the card reader is clicked
        /**cardReader.setOnMouseClicked(event -> {
         try {
         root = FXMLLoader.load(getClass().getResource("card-selection-view.fxml"));
         Stage cardSelection = new Stage();
         StackPane layout = new StackPane();
         Scene scene = new Scene(root);
         cardSelection.setTitle("Select your Card");
         cardSelection.setScene(scene);
         cardSelection.getIcons().add(new Image(ATM.class.getResourceAsStream("/cardIcon.png")));
         // set the stage to be non-resizable
         cardSelection.setResizable(false);
         cardSelection.show();
         } catch (IOException e) {
         throw new RuntimeException(e);
         }
         });**/
        // Textbox prompt for user to key in the card number
        cardReader.setOnMouseClicked(event -> {
            setImageSize(cardReader, 428, 369);
            //Image cardReaderWCardImg = updateImage("/img/card_reader_w_card.png");
            cardReader.setImage(cardReaderWCardImg);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setGraphic(null);
            dialog.setTitle("Card Details");
            dialog.setHeaderText("Please enter your card number");
            dialog.setContentText("Card Number: ");
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.setOnAction(e ->{
                setImageSize(cardReader, 428, 191);
                cardReader.setImage(cardReaderImg);
            });
            Optional<String> pinCode = dialog.showAndWait();
            if (pinCode.isPresent()) {
                String input = pinCode.get();
                System.out.println(input);
                try {
                    atm.changeScene("atm-pin-view.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // TODO: connect to database and check if card exist, if yes, load the next scene, else alert user
            }
        });
    }
}