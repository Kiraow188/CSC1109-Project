package com.sitatm.sitatm;

import com.google.firebase.auth.FirebaseAuth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class ATMController {
    @FXML
    private ImageView cardReader;
    @FXML
    private ImageView receiptPrinter;

    @FXML
    private TextField emailField;
//    @FXML
//    private PasswordField passwordField;
    @FXML
    private Button loginButton;
//    @FXML
//    private Label errorMessageLabel;


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

//    @FXML
//    private void handleLoginButtonAction(ActionEvent event) {
//        // Get the email and password entered by the user
//        String email = emailField.getText();
//        String password = passwordField.getText();
//
//        // Authenticate the user with Firebase Authentication
//        FirebaseAuth.getInstance()
//                .signInWithEmailAndPassword(email, password)
//                .addOnSuccessListener(authResult -> {
//                    // Authentication successful, show the ATM screen
//                    showATMScreen();
//                })
//                .addOnFailureListener(e -> {
//                    // Authentication failed, show an error message
//                    errorMessageLabel.setText("Invalid email or password");
//                });
//    }

    private void showATMScreen() {
        try {
            // Load the FXML file for the ATM screen
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("atm-screen.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new Scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Set the scene to the stage and show the stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}