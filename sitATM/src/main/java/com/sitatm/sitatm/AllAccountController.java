package com.sitatm.sitatm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class AllAccountController {
    ATM atm = new ATM();
    @FXML
    private Button btnHome;
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account account = holder.getAccount();
    private final String fxmlFile = "atm-all-account-view.fxml";
    DecimalFormat df = new DecimalFormat("#.##");
    @FXML
    private VBox accountVBox;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
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
    private void initialize() throws SQLException {
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

        String userID = account.getUserId();
        ResultSet accountSet = db.executeQuery("SELECT account_number from account where user_id = "+userID);
        ObservableList<String> accountNumbers = FXCollections.observableArrayList();
        while (accountSet.next()) {
            String accountNumber = accountSet.getString("account_number");
            accountNumbers.add(accountNumber);
        }

        int rowCount = accountNumbers.size();
        System.out.println("Number of rows returned: " + rowCount);
        accountVBox.setSpacing(30);
        for (String accountNumber : accountNumbers) {
            ResultSet balanceSet = db.executeQuery("SELECT * FROM customer c JOIN account a ON c.user_id = a.user_id JOIN ( SELECT * FROM transaction WHERE account_number = "+accountNumber+" ORDER BY transaction_id DESC LIMIT 1) t ON a.account_number = t.account_number WHERE a.account_number = "+accountNumber);
            if (balanceSet.next()){
                double latestBal = balanceSet.getDouble("balance_amt");
                String accType = balanceSet.getString("account_type");
                // Programmatically create buttons
                Button button = new Button(accType+"\n"+accountNumber+"\n$"+df.format(latestBal));
                button.setPrefWidth(490);
                button.setPrefHeight(130);
                button.setAlignment(Pos.CENTER_LEFT);
                button.setStyle("-fx-font-size: 24;-fx-background-color: #F2CD60;-fx-text-fill: white;");
                // Add the button to the vbox
                accountVBox.getChildren().add(button);
            }
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
}
