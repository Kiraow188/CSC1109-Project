package com.atm;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Main {
    @FXML
    private ImageView cardReader;
    
    private int currentImage = 0;
    private Image[] images = new Image[] {
        new Image(getClass().getResourceAsStream("/cardInsert.png")),
        new Image(getClass().getResourceAsStream("/insertCard.png")),

    };

    /*@FXML
    private void handleCardReaderClick() {
        cardReader.setImage(images[currentImage]);
    } */
    
    @FXML
    private void initialize() {
        cardReader.setOnMouseClicked(event -> {
            cardReader.setImage(images[currentImage]);
        });
        
/*         cardReader.setOnMouseEntered(event ->{
            cardReader.setImage(images[currentImage]);
          });
        cardReader.setOnMouseExited(event ->{
               cardReader.setImage(images[currentImage+1]);
         }); */
    }
}