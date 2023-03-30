package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController {
    ATM atm = new ATM();
    @FXML
    private Button btnExit;
    @FXML
    private Button chkBalBtn;
    @FXML
    private Label welcomeMsg;
    @FXML
    private Button btnWithdrawl;
    @FXML
    private Button btnFundTransfer;
    @FXML
    private Button btnCashDeposit;
    @FXML
    private Button btnAllAcc;
    @FXML
    private Button btn50;
    @FXML
    private Button btn80;
    @FXML
    private Button btn100;
    @FXML
    private Button btn150;
    @FXML
    private Button btn200;
    @FXML
    private Button btn300;
    @FXML
    private Button btnMreSvc;

    private final String fxmlFile = "atm-main-view.fxml";
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Customer c = holder.getUser();
    private Account a = holder.getAccount();

    public String getGreetingMessage(LocalTime time) {
        ResourceBundle messages = l.getLocale();
        int hour = time.getHour();

        if (hour >= 6 && hour < 12) {
            return messages.getString("greeting.morning");
        } else if (hour >= 12 && hour < 18) {
            return messages.getString("greeting.afternoon");
        } else {
            return messages.getString("greeting.evening");
        }
    }
    @FXML
    private void cashWithdrawalAction(ActionEvent event){
        try {
            atm.changeScene("atm-cash-withdrawal-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void fundTransferAction(ActionEvent event){
        try {
            atm.changeScene("atm-fund-transfer-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void AllAccountAction(ActionEvent event) {
        try {
            atm.changeScene("atm-all-account-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    @FXML
    private void CashDepositAction(ActionEvent event){
        try {
            atm.changeScene("atm-cash-deposit-view.fxml",l.getLocale());
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }
    @FXML
    private void MoreServicesAction(ActionEvent event){
        try{
            atm.changeScene("atm-more-services-view.fxml",l.getLocale());
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }

    @FXML
    private void exitAction(ActionEvent event) {
        try {
            atm.changeScene("atm-exit-view.fxml",l.getLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void chkBalAction(ActionEvent event) {
        try{
            Database db = holder.getDatabase();
            String accNum = a.getAccountNo();
            ResultSet resultSet = db.executeQuery("SELECT * FROM customer c JOIN account a ON c.user_id = a.user_id JOIN ( SELECT * FROM transaction WHERE account_number = "+accNum+" ORDER BY transaction_id DESC LIMIT 1) t ON a.account_number = t.account_number WHERE a.account_number = "+accNum);
            if (resultSet.next()){
                String latestBal = resultSet.getString("balance_amt");
                chkBalBtn.setText(l.getLocale().getString("BalanceText") +": $"+ latestBal);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //chkBalBtn.setText("Balance: $dummy");
    }
    @FXML
    private void fastCashWithdrawal(ActionEvent event){
        Button clickedButton = (Button) event.getSource();
        String buttonValue = clickedButton.getText().substring(1);

        String accNo = a.getAccountNo();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SIT ATM: Fast Cash Withdrawal Confirmation");
        alert.setHeaderText("Please confirm the following action: \n\nWithdraw: $"+buttonValue+"\nFrom Account Number: "+a.getAccountNo());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            try {
                int withdraw_amount = Integer.parseInt(buttonValue);
                double accReBalance = 0;
                String actionStatement = "FAST CASH WITHDRAWAL";
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
                    withdrawalError.setTitle("SIT ATM: Fast Cash Withdrawal Error");
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
                        succAlert.setTitle("SIT ATM: Fast Cash Withdrawal Successful!");
                        succAlert.setGraphic(null);
                        succAlert.setHeaderText("$"+withdraw_amount+" has been withdrawn successfully!" +
                                "\nCurrent Available Balance: $"+accReBalance+
                                "\n\nThank you for banking with us!");
                        succAlert.showAndWait();
                    }
                    else{
                        Alert errAlert = new Alert(Alert.AlertType.ERROR);
                        errAlert.setTitle("SIT ATM: Fast Cash Withdrawal Failure!");
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
                System.out.println("An exception has occurred: " + e);
            }
        } else {
            System.out.println("SITATM: Deposit cancelled");
        }
    }

    public void setWelcomeMsg(){
        // Get the current time and run it through the getGreetingMessage function
        LocalTime currentTime = LocalTime.now();
        String greeting = getGreetingMessage(currentTime);
        // Retrieve customer data using Singleton class
        String name = c.getfName();
        welcomeMsg.setText(greeting+", "+name);
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
    public void initialize() throws IOException {
        setWelcomeMsg();
        // Set the style of the button when the mouse is over it
        btnWithdrawl.setOnMouseEntered(e -> btnWithdrawl.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnCashDeposit.setOnMouseEntered(e -> btnCashDeposit.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnFundTransfer.setOnMouseEntered(e -> btnFundTransfer.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        chkBalBtn.setOnMouseEntered(e -> chkBalBtn.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn50.setOnMouseEntered(e -> btn50.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn80.setOnMouseEntered(e -> btn80.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn100.setOnMouseEntered(e -> btn100.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn150.setOnMouseEntered(e -> btn150.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn200.setOnMouseEntered(e -> btn200.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn300.setOnMouseEntered(e -> btn300.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnAllAcc.setOnMouseEntered(e -> btnAllAcc.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnMreSvc.setOnMouseEntered(e -> btnMreSvc.setStyle("-fx-background-color: white; -fx-text-fill: #F2CD60;-fx-font-weight: bold;-fx-font-size: 16px;"));
        // Set the style of the button when the mouse leaves it
        btnWithdrawl.setOnMouseExited(e -> btnWithdrawl.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnCashDeposit.setOnMouseExited(e -> btnCashDeposit.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnFundTransfer.setOnMouseExited(e -> btnFundTransfer.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        chkBalBtn.setOnMouseExited(e -> chkBalBtn.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn50.setOnMouseExited(e -> btn50.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn80.setOnMouseExited(e -> btn80.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn100.setOnMouseExited(e -> btn100.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn150.setOnMouseExited(e -> btn150.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn200.setOnMouseExited(e -> btn200.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btn300.setOnMouseExited(e -> btn300.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnAllAcc.setOnMouseExited(e -> btnAllAcc.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));
        btnMreSvc.setOnMouseExited(e -> btnMreSvc.setStyle("-fx-background-color: #F2CD60; -fx-text-fill: white;-fx-font-weight: bold;-fx-font-size: 16px;"));

        URL imageUrl = getClass().getResource("/img/exit.png");
        Image image = new Image(imageUrl.toString());
        ImageView exitImgView = new ImageView(image);

        btnExit.setGraphic(exitImgView);
        btnExit.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-color: transparent;");
    }
}
