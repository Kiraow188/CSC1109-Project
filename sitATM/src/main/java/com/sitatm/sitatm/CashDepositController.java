package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CashDepositController {
    ATM atm = new ATM();
    @FXML
    private TextField txtFieldAmt;
    @FXML
    private ChoiceBox<String> accDrpDwn;
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private Account a = holder.getAccount();
    private Database db = holder.getDatabase();
    private final String fxmlFile = "atm-cash-deposit-view.fxml";
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
    private void deposit(ActionEvent event) throws SQLException {
        if (accDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Deposit Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please select an account number to withdraw from!");
            withdrawConfirmation.showAndWait();
        }
        else if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Deposit Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want to perform this action?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try {
                    String accNo = accDrpDwn.getValue();
                    int deposit_amount = Integer.parseInt(txtFieldAmt.getText());
                    double accReBalance = 0;
                    String actionStatement = "ATM DEPOSIT";
                    ResultSet AccRetrieval = db.executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accNo + " AND account.account_number = " + accNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;");
                    while (AccRetrieval.next()) {
                        accReBalance = AccRetrieval.getDouble("balance_amt");
                    }
                    accReBalance = accReBalance + deposit_amount;
                    String depositAmountBalanceQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                    PreparedStatement depositAmountBalance = db.getConnection().prepareStatement(depositAmountBalanceQuery);
                    depositAmountBalance.setString(1, accNo);
                    depositAmountBalance.setDate(2,
                            java.sql.Date.valueOf(java.time.LocalDate.now()));
                    depositAmountBalance.setString(3, actionStatement);
                    depositAmountBalance.setInt(4, 0);
                    depositAmountBalance.setDouble(5, deposit_amount);
                    depositAmountBalance.setDouble(6, 0);
                    depositAmountBalance.setDouble(7, accReBalance);
                    if (db.executeUpdate(depositAmountBalance) > 0) {
                        Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success!");
                        alert.setHeaderText(null);
                        alert.setContentText("You have successfully deposited: $"+deposit_amount+"\n Available Balance: $"+accReBalance);
                        alert.showAndWait();
                        try {
                            atm.changeScene("atm-main-view.fxml",l.getLocale());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                System.out.println("An exception has occured: " + e);
                }
            } else {
                System.out.println("Debuggy{121}: walao waste my time!");
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
    @FXML
    public void initialize() throws SQLException {
        System.out.println("Hi it's me debuggy!");
        String userID = a.getUserId();
        System.out.println("Debuggy{139}: Current UserID is - "+userID);
        try{
            ResultSet resultSet = db.executeQuery("SELECT account_number FROM account where user_id = "+userID);
            List<String> accountNumbers = new ArrayList<>();
            while (resultSet.next()) {
                String acc = resultSet.getString("account_number");
                accountNumbers.add(acc);
            }
            accDrpDwn.getItems().addAll(accountNumbers);
        } catch (SQLException e ){
            System.out.println("SQL Exception caught: " + e);
        }
    }
}
