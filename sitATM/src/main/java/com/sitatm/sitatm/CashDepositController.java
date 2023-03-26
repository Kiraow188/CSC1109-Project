package com.sitatm.sitatm;

import javafx.event.ActionEvent;
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
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CashDepositController {
    ATM atm = new ATM();
    @FXML
    private TextField txtFieldAmt;
    @FXML
    private Button btnConfirm;
    @FXML
    private ChoiceBox<String> accDrpDwn;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
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
            atm.changeScene("atm-exit-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void deposit(ActionEvent event) throws SQLException {
        if (accDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Cash Deposit Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please select an account number to withdraw from!");
            withdrawConfirmation.showAndWait();
        }
        else if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
            withdrawConfirmation.setTitle("SIT ATM: Cash Deposit Confirmation");
            withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIT ATM: Cash Deposit");
            alert.setHeaderText("Please confirm the following action: " +
                    "\n\nDeposit: $"+txtFieldAmt.getText()+"\nTo Account Number: "+accDrpDwn.getValue());

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
                        succAlert.setTitle("Success!");
                        succAlert.setHeaderText(null);
                        succAlert.setContentText("$"+deposit_amount+" has been deposited successfully.\nCurrent Available Balance: $"+accReBalance+
                                "\n\nThank you for banking with us!");
                        succAlert.showAndWait();
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
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) {
                return change;
            }
            return null;
        };
        txtFieldAmt.setTextFormatter(new TextFormatter<String>(filter));
    }
}
