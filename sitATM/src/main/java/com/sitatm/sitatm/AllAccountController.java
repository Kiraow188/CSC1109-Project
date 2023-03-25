package com.sitatm.sitatm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllAccountController {
    ATM atm = new ATM();
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account account = holder.getAccount();
    private final String fxmlFile = "atm-all-account-view.fxml";
    @FXML
    private VBox accountVBox;
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
            atm.changeScene("atm-exit-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void initialize() throws SQLException {
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
                String latestBal = balanceSet.getString("balance_amt");
                String accType = balanceSet.getString("account_type");
                // Programmatically create buttons
                Button button = new Button(accType+"\n"+accountNumber+"\n$"+latestBal);
                button.setPrefWidth(490);
                button.setPrefHeight(130);
                button.setAlignment(Pos.CENTER_LEFT);
                button.setStyle("-fx-font-size: 24;-fx-background-color: #F2CD60;");
                //button.setStyle("-fx-background-color: #F2CD60;");
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
