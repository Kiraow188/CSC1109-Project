package com.sitatm.sitatm;

import java.sql.ResultSet;

public class Account {
    private String accountNo;
    private String userId;
    private String pin;
    private String salt;
    private String accountType;
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean checkAccountStatus(String accountNo){
        Database db = UserHolder.getInstance().getDatabase();
        boolean isDeactivated = true;
        try {
            ResultSet resultSet = db
                    .executeQuery("SELECT deactivation_date FROM account WHERE account_number=" + accountNo);
            if (resultSet.next()) {
                String deactivationDate = resultSet.getString("deactivation_date");
                if (deactivationDate == null) {
                    return isDeactivated = false;
                }
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeactivated;
    }

}
