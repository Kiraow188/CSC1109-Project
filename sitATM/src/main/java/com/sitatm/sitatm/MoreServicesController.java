package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;

public class MoreServicesController {
    ATM atm = new ATM();
    @FXML
    private Button btnHome;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private final String fxmlFile = "atm-more-services-view.fxml";
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
            atm.changeScene("atm-exit-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void changePinAction(ActionEvent event){
        try {
            atm.changeScene("atm-change-pin-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void initialize(){
        // Set the style of the button when the mouse is over it
        btnHome.setOnMouseEntered(e -> btnHome.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 24px;"));
        // Set the style of the button when the mouse leaves it
        btnHome.setOnMouseExited(e -> btnHome.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 24px;"));

        URL imageUrlExit = getClass().getResource("/img/exit.png");
        Image exitImage = new Image(imageUrlExit.toString());
        ImageView exitImgView = new ImageView(exitImage);
        URL imageUrlBack = getClass().getResource("/img/left-arrow.png");
        Image backImage = new Image(imageUrlBack.toString());
        ImageView backImgView = new ImageView(backImage);

        btnExit.setGraphic(exitImgView);
        btnExit.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-color: transparent;");
        btnBack.setGraphic(backImgView);
        btnBack.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-color: transparent;");
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
