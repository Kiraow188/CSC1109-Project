package com.sitatm.sitatm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ApplyLoanController {
    ATM atm = new ATM();
    private Singleton holder = Singleton.getInstance();
    private Localization l = holder.getLocalization();
    private Database db = holder.getDatabase();
    private Account account = holder.getAccount();
    private Customer customer = holder.getUser();
    private final String fxmlFile = "atm-apply-loan-view.fxml";
    @FXML
    private Button btnExit;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnConfirm;
    @FXML
    private ChoiceBox<String> accDrpDwn;
    @FXML
    private TextField txtFieldAmt;
    @FXML
    private ChoiceBox<String> periodDrpDwn;
    @FXML
    private void backAction(ActionEvent event){
        try {
            atm.changeScene("atm-more-services-view.fxml",l.getLocale());
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
    private void applyLoan(ActionEvent event) throws SQLException, IOException {
        if (accDrpDwn.getValue() == null) {
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Loan Application Warning");
            withdrawConfirmation.setHeaderText("Please select an account number to withdraw from!");
            withdrawConfirmation.showAndWait();
        } else if (txtFieldAmt.getText().equals("")) {
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Loan Application Warning");
            withdrawConfirmation.setHeaderText("Please enter an amount!");
            withdrawConfirmation.showAndWait();
        } else if (Integer.parseInt(txtFieldAmt.getText()) < 1) {
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Loan Application Warning");
            withdrawConfirmation.setHeaderText("You cannot loan an amount less than $0");
            withdrawConfirmation.showAndWait();
        } else if (periodDrpDwn.getValue() == null){
            Alert withdrawConfirmation = new Alert(Alert.AlertType.WARNING);
            withdrawConfirmation.setTitle("SIT ATM: Loan Application Warning");
            withdrawConfirmation.setHeaderText("Please select a loan period from the drop down list!");
            withdrawConfirmation.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIT ATM: Loan Application Confirmation");
            alert.setHeaderText("Please confirm the following action: \n\nLoan Application\nPrinciple Amount: $" + txtFieldAmt.getText() + "\nFor Account Number: " + accDrpDwn.getValue());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                Boolean isDeactived = account.checkAccountStatus(account.getAccountNo());
                if (!isDeactived) {
                    String accReNo = accDrpDwn.getValue();
                    int loanAmt = Integer.parseInt(txtFieldAmt.getText());

                    int loanType = 0;
                    if (periodDrpDwn.getValue().equals("3 Months")){
                        loanType = 3;
                    }else if (periodDrpDwn.getValue().equals("12 Months")){
                        loanType = 12;
                    }else{loanType = 24;}
                    double intRate = 3.88;
                    double proRate = 1.00;
                    double lateRate = 5;
                    int count = 0;
                    ResultSet checkLoanIP = db
                            .executeQuery(
                                    "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                            + accReNo + " AND loan.status = 'PENDING'"); //
                    while (checkLoanIP.next()) {
                        count++;
                    }
                    checkLoanIP = db
                            .executeQuery(
                                    "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                            + accReNo + " AND loan.status = 'APPROVED'"); //
                    while (checkLoanIP.next()) {
                        count++;
                    }

                    if (count > 0) {
                        Alert withdrawConfirmation = new Alert(Alert.AlertType.ERROR);
                        withdrawConfirmation.setTitle("SIT ATM: Loan Application Error");
                        withdrawConfirmation.setGraphic(null);
                        withdrawConfirmation.setHeaderText("Account Number: " + accReNo + " has an ongoing loan.");
                        withdrawConfirmation.showAndWait();
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        double calcDebt = (loanAmt / ((Math.pow(1 + ((intRate / 100) / 12), loanType) - 1)
                                / (((intRate / 100) / 12) * (Math.pow(1 + ((intRate / 100) / 12), loanType)))));
                        String formattedResult = decimalFormat.format(calcDebt);
                        calcDebt = Double.parseDouble(formattedResult);
                        Alert finalConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        finalConfirmation.setTitle("SIT ATM: Loan Application Confirmation");
                        finalConfirmation.setHeaderText("Break down of monthly instalment \n\nPrinciple Amount: $"+loanAmt+"\nLoan Duration: "+loanType+" Months\nMonthly Repayment: $"+calcDebt+" per month. \n\nDo you wish to proceed?");
                        Optional<ButtonType> cfmResult = finalConfirmation.showAndWait();
                        if (cfmResult.isPresent() && cfmResult.get() == ButtonType.OK){
                            DecimalFormat df = new DecimalFormat("#.##");
                            calcDebt = Double.valueOf(df.format(calcDebt));

                            String depositAmountBalanceQuery = "INSERT INTO `loan`(`loan_id`, `account_number`, `principle_amt`, `interest_rate`, `duration`, `debt`, `date_created`, `repayment_date`, `status`) VALUES (?,?,?,?,?,?,?,?,?);";
                            PreparedStatement depositAmountBalance = db
                                    .getConnection().prepareStatement(depositAmountBalanceQuery);
                            depositAmountBalance.setInt(1, 0);
                            depositAmountBalance.setString(2, accReNo);
                            depositAmountBalance.setDouble(3, loanAmt);
                            depositAmountBalance.setDouble(4, intRate);
                            depositAmountBalance.setInt(5, loanType);
                            depositAmountBalance.setDouble(6, calcDebt * loanType);
                            depositAmountBalance.setDate(7,
                                    java.sql.Date.valueOf(java.time.LocalDate.now()));
                            depositAmountBalance.setDate(8, null);
                            depositAmountBalance.setString(9, "PENDING");
                            int rowsDptAffected = depositAmountBalance.executeUpdate();
                            if (rowsDptAffected <= 0) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("SIT ATM: Loan Application Failure");
                                errorAlert.setContentText("Something went wrong while processing your loan. Please try again later.");
                                errorAlert.showAndWait();
                            } else {
                                Alert succAlert = new Alert(Alert.AlertType.INFORMATION);
                                succAlert.setTitle("SIT ATM: Loan Application Success");
                                succAlert.setGraphic(null);
                                succAlert.setHeaderText("Your loan application has been submitted successfully!");
                                succAlert.showAndWait();
                                Email.sendLoanApplicationEmail(customer.getfName(),customer.getEmail());
                                atm.changeScene("atm-main-view",l.getLocale());
                            }
                        }
                    }
                }
                else{
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("SIT ATM: Loan Application Failure");
                    errorAlert.setHeaderText("You cannot apply for loan on a deactivated account!");
                    errorAlert.showAndWait();
                }
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
    public void initialize(){
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
        String userID = account.getUserId();
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
        ObservableList<String> choices = FXCollections.observableArrayList(
                "3 Months", "12 Months", "24 Months");
        // Add the choices to the Period ChoiceBox
        periodDrpDwn.setItems(choices);
        // Limit ToAccTxtBox to 9 characters

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
