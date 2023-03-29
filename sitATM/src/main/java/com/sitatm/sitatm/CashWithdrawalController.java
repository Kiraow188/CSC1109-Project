package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.text.DecimalFormat;

public class CashWithdrawalController {
    ATM atm = new ATM();
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
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account a = holder.getAccount();
    private Customer c = holder.getUser();
    private final String fxmlFile = "atm-cash-withdrawal-view.fxml";
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
        Date date = java.sql.Date.valueOf(java.time.LocalDate.now());
        if (txtFieldAmt.getText().equals("")){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Warning");
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        }
        else if (Integer.parseInt(txtFieldAmt.getText()) < 20){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Warning");
            withdrawConfirmation.setHeaderText("Minimum withdrawal amount is $20!");
            withdrawConfirmation.showAndWait();
        }
        else if (accDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Withdrawal Warning");
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
                        withdrawalError.setHeaderText("You do not have sufficient funds to perform this action.");
                        withdrawalError.showAndWait();
                    }else {
                        String withdrawalQuery = "INSERT INTO transaction(account_number, date, transaction_details, chq_no, withdrawal_amt, deposit_amt, balance_amt) VALUES (?,?,?,?,?,?,?)";
                        PreparedStatement withdrawal = db.getConnection().prepareStatement(withdrawalQuery,Statement.RETURN_GENERATED_KEYS);
                        withdrawal.setString(1, accNo);
                        withdrawal.setDate(2, date);
                        withdrawal.setString(3, actionStatement);
                        withdrawal.setInt(4, 0);
                        withdrawal.setDouble(5, withdraw_amount);
                        withdrawal.setDouble(6, 0);
                        withdrawal.setDouble(7, accReBalance);
                        if (db.executeUpdate(withdrawal) > 0) {
                            // Get the generated keys
                            try (ResultSet rs = withdrawal.getGeneratedKeys()) {
                                if (rs.next()) {
                                    int transactionId = rs.getInt(1);
                                    System.out.println("Transaction ID: " + transactionId);
                                    receiptPrinter.printReceipt(c.getfName(),date, a.getAccountNo(),transactionId,withdraw_amount,accReBalance,1);
                                }
                            }
                            Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                            succAlert.setTitle("SIT ATM: Withdrawal Successful!");
                            succAlert.setGraphic(null);
                            succAlert.setHeaderText("$"+withdraw_amount+" has been withdrawn successfully from Account no. "+ accNo +
                                    "\nCurrent Available Balance: $"+df.format(accReBalance)+
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
                            errAlert.setTitle("SIT ATM: Cash Withdrawal Failure!");
                            errAlert.setHeaderText("Something went wrong, please try again later.");
                            errAlert.showAndWait();
                            try{
                                atm.changeScene(fxmlFile,l.getLocale());
                            } catch (IOException e) {
                                System.out.println("IOException caught: "+e);
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
