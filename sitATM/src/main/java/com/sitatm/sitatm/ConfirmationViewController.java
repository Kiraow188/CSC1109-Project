package com.sitatm.sitatm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class ConfirmationViewController {
    ATM atm = new ATM();
    private UserHolder holder = UserHolder.getInstance();
    private Localization l = holder.getLocalization();
    private final String fxmlFile = "atm-confirmation-view.fxml";
    @FXML
    private Button btnExit;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subLabel;
    @FXML
    private AnchorPane labelAnchor;
    @FXML
    private TextFlow txtFlow = new TextFlow();

    @FXML
    private void exitAction(ActionEvent event){
        try {
            atm.changeScene("atm-exit-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void cwConfirmation(String amount){
        // TODO: Check user's balance to see if there is sufficient funds before allowing withdrawal
        // Success Block
        Text titleText = new Text("You have successfully withdrawn $" + amount + " from your account.\n");
        Text subText = new Text("Do you wish to continue with our other services?");
        Text spacers = new Text("\n");
        titleText.setFont(Font.font("Arial", 36));
        subText.setFont(Font.font("Arial", 32));
        txtFlow.getChildren().add(titleText);
        txtFlow.getChildren().add(spacers);
        txtFlow.getChildren().add(subText);
        txtFlow.setPadding(new Insets(0));
        txtFlow.setTextAlignment(TextAlignment.CENTER);
    }
    @FXML
    private void initialize(){

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
}
