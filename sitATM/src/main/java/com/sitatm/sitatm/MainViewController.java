package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalTime;

public class MainViewController {
    ATM atm = new ATM();
    Customer customer = new Customer();
    @FXML
    private Button btnExit;
    @FXML
    private Button chkBalBtn;
    @FXML
    private Label welcomeMsg;

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
            atm.changeScene("atm-cash-withdrawal-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void fundTransferAction(ActionEvent event){
        try {
            atm.changeScene("atm-fund-transfer-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void AllAccountAction(ActionEvent event) {
        try {
            atm.changeScene("atm-all-account-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    @FXML
    private void CashDepositAction(ActionEvent event){
        try {
            atm.changeScene("atm-cash-deposit-view.fxml");
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }
    @FXML
    private void MoreServicesAction(ActionEvent event){
        try{
            atm.changeScene("atm-more-services-view.fxml");
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
        LocalTime currentTime = LocalTime.now();
        String greeting = getGreetingMessage(currentTime);
        UserHolder holder = UserHolder.getInstance();
        Customer c = holder.getUser();
        String name = c.getfName();
        welcomeMsg.setText(greeting+", "+name);
    }
}
