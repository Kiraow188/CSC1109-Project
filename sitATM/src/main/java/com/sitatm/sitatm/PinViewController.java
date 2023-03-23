package com.sitatm.sitatm;

import com.itextpdf.text.pdf.fonts.otf.Language;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PinViewController {
    ATM atm = new ATM();
    @FXML
    private PasswordField pinTextBox;
    @FXML
    private Button cancelButton;
    Account user = new Account();

    public void setUser(Account user) {
        this.user = user;
    }

    @FXML
    private void keypadButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonValue = button.getText();
        // debugging
        System.out.println(buttonValue);
        String currentPassword = pinTextBox.getText();
        pinTextBox.setText(currentPassword + buttonValue);
    }

    @FXML
    private void clearButtonAction(ActionEvent event){
        pinTextBox.clear();
    }

    @FXML
    private void cancelButtonAction(ActionEvent event){
        // TODO: either close this stage or close the program
        try {
            atm.changeScene("atm-login-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void confirmButtonAction(ActionEvent event) throws NoSuchAlgorithmException, IOException, SQLException {
        Database db = new Database();
        PinHash pinHash = new PinHash();
        Customer customer = new Customer();
        Localization localization = new Localization();
        System.out.println(pinTextBox.getText()+"\n"+user.getSalt()+"\n"+user.getPin());
        boolean doesPinMatch = PinHash.hashMatching(pinTextBox.getText(),user.getSalt(),user.getPin());
        if (doesPinMatch){
            System.out.println(user.getUserId());
            ResultSet resultSet = db.executeQuery("SELECT * from customer where user_id = '"+user.getUserId()+"'");
            if (resultSet.next()){
                UserHolder holder = UserHolder.getInstance();
                String custName = resultSet.getString("full_name");
                String custID = resultSet.getString("user_id");
                customer.setfName(custName);
                customer.setUserID(custID);
                holder.setUser(customer);
                holder.setLocalization(localization);
                ResourceBundle bundle = localization.getLocale();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("atm-main-view.fxml"),bundle);
                Parent root = loader.load();
                MainViewController controller = loader.getController();
                controller.setWelcomeMsg();
                //controller.setEN();
                //controller.setCustomer(customer);
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            //atm.changeScene("atm-main-view.fxml");
        }else{
            System.out.println("Incorrect pin number, please try again.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Pin Number");
            alert.setContentText("The pin number you entered is incorrect. Please try again.");
            alert.showAndWait();
            pinTextBox.clear();
        }
    }
}
