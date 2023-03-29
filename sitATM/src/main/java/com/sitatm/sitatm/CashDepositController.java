package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Account a = holder.getAccount();
    private Customer c = holder.getUser();
    private Database db = holder.getDatabase();
    private final String fxmlFile = "atm-cash-deposit-view.fxml";
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
    private void deposit(ActionEvent event) throws SQLException {
        Date date = java.sql.Date.valueOf(java.time.LocalDate.now());
        if (accDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Cash Deposit Warning");
            //withdrawConfirmation.setGraphic(null);
            withdrawConfirmation.setHeaderText("Please select an account number to withdraw from!");
            withdrawConfirmation.showAndWait();
        }
        else if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Cash Deposit Warning");
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIT ATM: Cash Deposit Confirmation");
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
                    String depositQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                    PreparedStatement deposit = db.getConnection().prepareStatement(depositQuery, Statement.RETURN_GENERATED_KEYS);
                    deposit.setString(1, accNo);
                    deposit.setDate(2, date);
                    deposit.setString(3, actionStatement);
                    deposit.setInt(4, 0);
                    deposit.setDouble(5, deposit_amount);
                    deposit.setDouble(6, 0);
                    deposit.setDouble(7, accReBalance);
                    if (db.executeUpdate(deposit) > 0) {
                        try (ResultSet rs = deposit.getGeneratedKeys()) {
                            if (rs.next()) {
                                int transactionId = rs.getInt(1);
                                System.out.println("Transaction ID: " + transactionId);
                                receiptPrinter.printReceipt(c.getfName(),date, a.getAccountNo(),transactionId,deposit_amount,accReBalance,0);
                            }
                        }
                        Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                        succAlert.setTitle("SIT ATM: Cash Deposit Successful!");
                        succAlert.setGraphic(null);
                        succAlert.setHeaderText("$"+deposit_amount+" has been deposited successfully to account no. "+accNo+"\nCurrent Available Balance: $"+df.format(accReBalance)+
                                "\n\nThank you for banking with us!");
                        succAlert.showAndWait();
                        try {
                            atm.changeScene("atm-main-view.fxml",l.getLocale());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        Alert errAlert = new Alert(Alert.AlertType.ERROR);
                        errAlert.setTitle("SIT ATM: Cash Deposit Failure!");
                        errAlert.setHeaderText("Something went wrong, please try again later.");
                        errAlert.showAndWait();
                        try{
                            atm.changeScene(fxmlFile,l.getLocale());
                        } catch (IOException e) {
                            System.out.println("IOException caught: "+e);
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
