package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainViewController {
    ATM atm = new ATM();
    Customer customer = new Customer();
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

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        chkBalBtn.setText("Balance: $dummy");
    }

    public void setWelcomeMsg(){
        // Get the current time and run it through the getGreetingMessage function
        LocalTime currentTime = LocalTime.now();
        String greeting = getGreetingMessage(currentTime);

        // Call the UserHolder class to retrieve the Global Customer Object
        UserHolder holder = UserHolder.getInstance();
        Customer c = holder.getUser();
        String name = c.getfName();
        welcomeMsg.setText(greeting+", "+name);
    }

    public void setZN() throws IOException{
        //Locale locale1 = new Locale("zn");
        /**
        locale = new Locale("zh");
        ResourceBundle bundle = ResourceBundle.getBundle("labelText", locale);
        FXMLLoader fxmlLoader = new FXMLLoader();
        atm.changeScene("atm-main-view.fxml", bundle);
         **/
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
