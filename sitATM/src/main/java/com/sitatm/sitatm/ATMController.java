package com.sitatm.sitatm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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

            AtomicBoolean validAccountNumber = new AtomicBoolean(false);
            while (!validAccountNumber.get()) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setGraphic(null);
                dialog.setTitle("Card Details");
                dialog.setHeaderText("Please enter your account number");
                dialog.setContentText("Account Number: ");
                Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                cancelButton.setOnAction(e ->{
                    validAccountNumber.set(true);
                    setImageSize(cardReader, 428, 191);
                    cardReader.setImage(cardReaderImg);
                });
                Optional<String> accNum = dialog.showAndWait();
                if (accNum.isPresent()) {
                    String input = accNum.get();
                    Database db = new Database();
                    try {
                        ResultSet resultSet = db.executeQuery("SELECT * FROM account where account_number='"+input+"'");
                        if (resultSet.next()){
                            Account user = new Account();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("atm-pin-view.fxml"));
                            Parent root = loader.load();
                            PinViewController controller = loader.getController();
                            String userID = resultSet.getString("user_id");
                            String pin = resultSet.getString("pin");
                            String salt = resultSet.getString("salt");
                            user.setAccountNo(input);
                            user.setUserId(userID);
                            user.setPin(pin);
                            user.setSalt(salt);
                            controller.setUser(user);
                            validAccountNumber.set(true);
                            Scene scene = new Scene(root);
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                            //atm.changeScene("atm-pin-view.fxml");
                        }else{
                            System.out.println("Incorrect Account number, please try again.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Invalid Account Number");
                            alert.setContentText("The account number you entered is incorrect. Please try again.");
                            alert.showAndWait();
                        }
                    } catch (IOException e) {
                        System.out.println("IO Exception Caught: " + e);
                    } catch (SQLException e ){
                        System.out.println("SQL Exception caught: " + e);
                    }
                } else {
                    validAccountNumber.set(true);
                }
            }
        });

    }
}