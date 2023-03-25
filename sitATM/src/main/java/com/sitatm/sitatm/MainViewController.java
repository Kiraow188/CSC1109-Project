package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Locale;
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

    //public Locale locale = new Locale("en");
    //public ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);
    private final String fxmlFile = "atm-main-view.fxml";
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Customer c = holder.getUser();
    private Account a = holder.getAccount();

    public static String getGreetingMessage(LocalTime time) {
        int hour = time.getHour();

        if (hour >= 6 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 18) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
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
            atm.changeScene("atm-exit-view.fxml");
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
                //chkBalBtn.setText("Balance: "+latestBal);
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
        System.out.println(buttonValue);

        String accNo = a.getAccountNo();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SIT ATM: Fast Cash Withdrawal");
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
                    }
                }
            } catch (Exception e) {
                System.out.println("An exception has occured: " + e);
            }
        } else {
            System.out.println("Debuggy{149}: walao waste my time!");
        }
    }

    public void setWelcomeMsg(){
        // Get the current time and run it through the getGreetingMessage function
        LocalTime currentTime = LocalTime.now();
        String greeting = getGreetingMessage(currentTime);
        // Retrieve customer data using UserHolder
        //Customer c = holder.getUser();
        String name = c.getfName();
        welcomeMsg.setText(greeting+", "+name);
        System.out.println("Debuggy{125}: Current user ID - "+a.getUserId());
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
    }
}
