package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.text.DecimalFormat;

public class FundTransferController {
    ATM atm = new ATM();
    @FXML
    private ChoiceBox<String> FromAccDrpDwn;
    @FXML
    private TextField ToAccTxtBox;
    @FXML
    private TextField TransfAmtTxtbox;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Account a = holder.getAccount();
    private Database db = holder.getDatabase();
    private final String fxmlFile = "atm-fund-transfer-view.fxml";
    DecimalFormat df = new DecimalFormat("#.##");

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
    private void fundTransfer(ActionEvent event) throws SQLException {
        String fromAccNo = FromAccDrpDwn.getValue();
        String toAccNo = ToAccTxtBox.getText();
        String actionStatementSender = "";
        String actionStatementReceiver = "";
        String receiverName = "";
        if (FromAccDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Fund Transfer Warning");
            withdrawConfirmation.setHeaderText("Please select an account number to transfer from!");
            withdrawConfirmation.showAndWait();
        }
        else if (ToAccTxtBox.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Fund Transfer Warning");
            withdrawConfirmation.setHeaderText("Please there the recipient's Account Number!");
            withdrawConfirmation.showAndWait();
        }
        else if (TransfAmtTxtbox.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Fund Transfer Warning");
            withdrawConfirmation.setHeaderText("Please enter the amount you want to transfer!");
            withdrawConfirmation.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIT ATM: Cash Withdrawal");
            alert.setHeaderText("Please confirm the following action: \n\nTransfer: $"+TransfAmtTxtbox.getText()+"\nFrom Account No. "+FromAccDrpDwn.getValue()+"\nTo Account No. "+ToAccTxtBox.getText());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                // First Check if the destination account exist in the database and is not deactivated.
                ResultSet resultSet = db.executeQuery("SELECT * from account where account_number = "+ToAccTxtBox.getText()+" AND deactivation_date IS NULL");
                if (resultSet.next()){
                    // Next is to check if customer has sufficient funds to transfer.
                    int transfer_amount = Integer.parseInt(TransfAmtTxtbox.getText());
                    double accReBalanceFrom = 0;
                    double accReBalanceTo = 0;

                    //Sender to have their bank balance decrease
                    ResultSet FromAccRetrieval = db.executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + fromAccNo + " AND account.account_number = " + fromAccNo
                                    + " ORDER BY transaction_id DESC LIMIT 1;");
                    while (FromAccRetrieval.next()) {
                        accReBalanceFrom = FromAccRetrieval.getDouble("balance_amt");
                        String senderName = FromAccRetrieval.getString("full_name");
                        actionStatementSender = "FUND TRANSFER FROM "+senderName;
                    }
                    accReBalanceFrom = accReBalanceFrom - transfer_amount;
                    if (accReBalanceFrom < transfer_amount) {
                        Alert withdrawalError = new Alert(Alert.AlertType.ERROR);
                        withdrawalError.setTitle("SIT ATM: Fund Transfer Error");
                        withdrawalError.setHeaderText("You do not have sufficient funds to perform this action.");
                        withdrawalError.showAndWait();
                    }else{
                        //Receiver to have their bank balance increase
                        ResultSet ToAccRetrieval = db.executeQuery(
                                "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                        + toAccNo + " AND account.account_number = " + toAccNo
                                        + " ORDER BY transaction_id DESC LIMIT 1;");
                        while (ToAccRetrieval.next()) {
                            accReBalanceTo = ToAccRetrieval.getDouble("balance_amt");
                            receiverName = ToAccRetrieval.getString("full_name");
                            actionStatementReceiver = "FUND TRANSFER TO "+receiverName;
                        }
                        accReBalanceTo = accReBalanceTo + transfer_amount;
                        //Perform the transfer -> First debit FROM then credit TO
                        //Debit FROM
                        String transferFromAmountBalanceQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                        PreparedStatement transferFromAmountBalance = db.getConnection().prepareStatement(transferFromAmountBalanceQuery);
                        transferFromAmountBalance.setString(1, fromAccNo);
                        transferFromAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        transferFromAmountBalance.setString(3, actionStatementReceiver);
                        transferFromAmountBalance.setInt(4, 0);
                        transferFromAmountBalance.setDouble(5, transfer_amount);
                        transferFromAmountBalance.setDouble(6, 0);
                        transferFromAmountBalance.setDouble(7, accReBalanceFrom);

                        //Credit TO
                        String transferToAmountBalanceQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                        PreparedStatement transferToAmountBalance = db.getConnection().prepareStatement(transferToAmountBalanceQuery);
                        transferToAmountBalance.setString(1, toAccNo);
                        transferToAmountBalance.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                        transferToAmountBalance.setString(3, actionStatementSender);
                        transferToAmountBalance.setInt(4, 0);
                        transferToAmountBalance.setDouble(5, 0);
                        transferToAmountBalance.setDouble(6, transfer_amount);
                        transferToAmountBalance.setDouble(7, accReBalanceTo);
                        if (db.executeUpdate(transferFromAmountBalance) > 0 && db.executeUpdate(transferToAmountBalance) > 0) {
                            Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                            succAlert.setTitle("SIT ATM: Fund Transfer Successful!");
                            succAlert.setHeaderText("You have successfully transferred: $"+transfer_amount+"\n To Acc no. "+toAccNo+" ("+receiverName+") and your current balance is: $"+df.format(accReBalanceFrom));
                            succAlert.showAndWait();
                            try {
                                atm.changeScene("atm-main-view.fxml",l.getLocale());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            Alert errAlert = new Alert(Alert.AlertType.ERROR);
                            errAlert.setTitle("SIT ATM: Fund Transfer Failure!");
                            errAlert.setHeaderText("Something went wrong, please try again later.");
                            errAlert.showAndWait();
                            try{
                                atm.changeScene(fxmlFile,l.getLocale());
                            } catch (IOException e) {
                                System.out.println("IOException caught: "+e);
                            }
                        }
                    }
                }else{
                    Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
                    withdrawConfirmation.setTitle("SIT ATM: Fund Transfer Error");
                    withdrawConfirmation.setGraphic(null);
                    withdrawConfirmation.setHeaderText("The account you have entered either does not exist or is deactivated.");
                    withdrawConfirmation.showAndWait();
                }
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

        // Limit ToAccTxtBox to 9 characters
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            String amountTxt = change.getControlNewText();
            if (text.matches("\\d{0,9}")) {
                return change;
            }
            if (amountTxt.matches("\\d")){
                return change;
            }
            return null;
        };
        ToAccTxtBox.setTextFormatter(new TextFormatter<String>(filter));
        TransfAmtTxtbox.setTextFormatter(new TextFormatter<String>(filter));
        String userID = a.getUserId();
        try{
            ResultSet resultSet = db.executeQuery("SELECT account_number FROM account where user_id = "+userID);
            List<String> accountNumbers = new ArrayList<>();
            while (resultSet.next()) {
                String acc = resultSet.getString("account_number");
                accountNumbers.add(acc);
            }
            FromAccDrpDwn.getItems().addAll(accountNumbers);
        } catch (SQLException e ){
            System.out.println("SQL Exception caught: " + e);
        }
    }
}
