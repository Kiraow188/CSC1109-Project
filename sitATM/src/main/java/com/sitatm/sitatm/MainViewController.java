package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private String fxmlFile = "atm-main-view.fxml";

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
            UserHolder holder = UserHolder.getInstance();
            Localization l = holder.getLocalization();
            ResourceBundle rb = l.bundle;
            System.out.println(rb);
            atm.changeScene("atm-cash-withdrawal-view.fxml",rb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void fundTransferAction(ActionEvent event){
        try {
            UserHolder holder = UserHolder.getInstance();
            Localization l = holder.getLocalization();
            ResourceBundle rb = l.bundle;
            System.out.println(rb);
            atm.changeScene("atm-fund-transfer-view.fxml",rb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void AllAccountAction(ActionEvent event) {
        try {
            UserHolder holder = UserHolder.getInstance();
            Localization l = holder.getLocalization();
            ResourceBundle rb = l.bundle;
            System.out.println(rb);
            atm.changeScene("atm-all-account-view.fxml",rb);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    @FXML
    private void CashDepositAction(ActionEvent event){
        try {
            UserHolder holder = UserHolder.getInstance();
            Localization l = holder.getLocalization();
            ResourceBundle rb = l.bundle;
            System.out.println(rb);
            atm.changeScene("atm-cash-deposit-view.fxml",rb);
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }
    @FXML
    private void MoreServicesAction(ActionEvent event){
        try{
            UserHolder holder = UserHolder.getInstance();
            Localization l = holder.getLocalization();
            ResourceBundle rb = l.bundle;
            System.out.println(rb);
            atm.changeScene("atm-more-services-view.fxml",rb);
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
        UserHolder holder = UserHolder.getInstance();
        Localization localization = new Localization();
        holder.setLocalization(localization);
        localization.setLocale(fxmlFile,"zh");
    }
    public void setEN() throws IOException{
        UserHolder holder = UserHolder.getInstance();
        Localization localization = new Localization();
        holder.setLocalization(localization);
        localization.setLocale(fxmlFile,"en");
    }
    public void initialize(){
        setWelcomeMsg();
        System.out.println("Debug: Hi im here");
    }
}
