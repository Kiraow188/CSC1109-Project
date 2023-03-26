package com.sitatm.sitatm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

public class ATMController {
    ATM atm = new ATM();
    UserHolder holder = UserHolder.getInstance();
    Database db = holder.getDatabase();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView cardReader;
    private Image cardReaderImg = updateImage("/img/card_reader_wo_card.png");
    private Image cardReaderWCardImg = updateImage("/img/card_reader_w_Ycard.png");

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
        // Textbox prompt for user to key in the card number
        cardReader.setOnMouseClicked(event -> {
            setImageSize(cardReader, 428, 369);
            cardReader.setImage(cardReaderWCardImg);

            AtomicBoolean validAccountNumber = new AtomicBoolean(false);
            while (!validAccountNumber.get()) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setGraphic(null);
                dialog.setTitle("SIT ATM");
                dialog.setHeaderText("Please enter your account number");
                dialog.setContentText("Account Number: ");

                // Apply a filter to accept digits only
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String text = change.getControlNewText();
                    if (text.matches("\\d{0,9}")) {
                        return change;
                    }
                    return null;
                };
                dialog.getEditor().setTextFormatter(new TextFormatter<>(filter));

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
                    //Database db = new Database();
                    try {
                        ResultSet resultSet = db.executeQuery("SELECT * FROM account where account_number='"+input+"'");
                        if (resultSet.next()){
                            Account user = new Account();
                            Boolean isDeactived = user.checkAccountStatus(input);
                            if (!isDeactived) {
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
                                holder.setAccount(user);
                                validAccountNumber.set(true);
                                Scene scene = new Scene(root);
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                                //atm.changeScene("atm-pin-view.fxml");
                            }
                            else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("SIT ATM");
                                errorAlert.setHeaderText("You cannot login using a deactivated account.\nPlease contact us for more information.");
                                errorAlert.showAndWait();
                            }
                        }else{
                            System.out.println("Incorrect Account number, please try again.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("SIT ATM");
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