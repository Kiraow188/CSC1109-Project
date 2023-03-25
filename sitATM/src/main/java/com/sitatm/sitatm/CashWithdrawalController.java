package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CashWithdrawalController {
    ATM atm = new ATM();
    //ConfirmationViewController cvc = new ConfirmationViewController();
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnConfirm;
    @FXML
    private TextField txtFieldAmt;
    @FXML
    private Button btnBackspace;
    @FXML
    private ChoiceBox<String> accDrpDwn;
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account a = holder.getAccount();
    private final String fxmlFile = "atm-cash-withdrawal-view.fxml";
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
    private void keypadButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonValue = button.getText();
        // debugging
        System.out.println(buttonValue);
        String currentPassword = txtFieldAmt.getText();
        txtFieldAmt.setText(currentPassword + buttonValue);
    }
    @FXML
    private void handleBackspaceButton(ActionEvent event) {
        int length = txtFieldAmt.getLength();
        if (length > 0) {
            txtFieldAmt.deleteText(length - 1, length);
        }
    }

    @FXML
    private void withdraw(ActionEvent event){
        /**
        Alert withdrawConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
        withdrawConfirmation.setGraphic(null);
        withdrawConfirmation.setHeaderText("Are you sure you want to withdraw $"+txtFieldAmt.getText()+" from your account?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        withdrawConfirmation.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = withdrawConfirmation.showAndWait();
        if (result.get() == yesButton){
            // Call your "Yes" function here
        } else {
            // Call your "No" function here
        }**/
        if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else if (Integer.parseInt(txtFieldAmt.getText()) < 20){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Minimum withdrawal amount is $20!");
            withdrawConfirmation.showAndWait();
        }
        else if (accDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please select an account number to withdraw from!");
            withdrawConfirmation.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIT ATM: Cash Withdrawal");
            alert.setHeaderText("Please confirm the following action: \n\nWithdraw: $"+txtFieldAmt.getText()+"\nFrom Account Number: "+accDrpDwn.getValue());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try {
                    String accNo = accDrpDwn.getValue();
                    int withdraw_amount = Integer.parseInt(txtFieldAmt.getText());
                    double accReBalance = 0;
                    String actionStatement = "ATM WITHDRAWAL";
                    ResultSet AccRetrieval = db.executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accNo + " AND account.account_number = " + accNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;");
                    while (AccRetrieval.next()) {
                        accReBalance = AccRetrieval.getDouble("balance_amt");
                    }
                    accReBalance = accReBalance - withdraw_amount;
                    if (accReBalance < withdraw_amount){
                        Alert withdrawalError = new Alert(Alert.AlertType.ERROR);
                        withdrawalError.setTitle("SIT ATM: Withdrawal Error");
                        withdrawalError.setGraphic(null);
                        withdrawalError.setHeaderText("You do not have sufficient funds to perform this action.");
                        withdrawalError.showAndWait();
                    }else {
                        String depositAmountBalanceQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                        PreparedStatement depositAmountBalance = db.getConnection().prepareStatement(depositAmountBalanceQuery);
                        depositAmountBalance.setString(1, accNo);
                        depositAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        depositAmountBalance.setString(3, actionStatement);
                        depositAmountBalance.setInt(4, 0);
                        depositAmountBalance.setDouble(5, withdraw_amount);
                        depositAmountBalance.setDouble(6, 0);
                        depositAmountBalance.setDouble(7, accReBalance);
                        if (db.executeUpdate(depositAmountBalance) > 0) {
                            Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                            succAlert.setTitle("Success!");
                            succAlert.setHeaderText(null);
                            succAlert.setContentText("$"+withdraw_amount+" has been withdrawn successfully!" +
                                    "\nCurrent Available Balance: $"+accReBalance+
                                    "\n\nThank you for banking with us!");
                            succAlert.showAndWait();
                            try {
                                atm.changeScene("atm-main-view.fxml",l.getLocale());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("An exception has occured: " + e);
                }
            } else {
                System.out.println("Debuggy{149}: walao waste my time!");
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
        // Set the style of the button when the mouse is over it
        btnConfirm.setOnMouseEntered(e -> btnConfirm.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 24px;"));
        // Set the style of the button when the mouse leaves it
        btnConfirm.setOnMouseExited(e -> btnConfirm.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 24px;"));

        System.out.println("Hi it's me debuggy!");
        String userID = a.getUserId();
        System.out.println("Debuggy{134}: Current UserID is - "+userID);
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
