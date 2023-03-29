package com.sitatm.sitatm;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Customer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private String userID;
    private String fName;
    private String nric;
    private String pnumber;
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{2}\\d{7}$");
    private String country;
    private String gender;
    private String dob;
    private String mNumber;
    private static final Pattern MNUMBER_PATTERN = Pattern.compile("[6|8-9]\\d{7}");
    private String address;
    private String email;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+]+@[A-Z0-9.]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);
    public Customer() {}
    public String getUserID() { return userID; }

    public void setUserID(String id) { this.userID = userID; }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
            this.nric = nric;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static boolean isValidNRIC(String nric) {
        if (nric.length() != 9) {
            return false;
        }

        char firstChar = nric.charAt(0);

        if (firstChar != 'S' && firstChar != 'T' && firstChar != 'F' && firstChar != 'G') {
            return false;
        }

        int[] multipliers = {2, 7, 6, 5, 4, 3, 2};
        int total = 0;

        for (int i = 1; i < 8; i++) {
            int digit = Character.digit(nric.charAt(i), 10);
            total += digit * multipliers[i - 1];
        }

        if (firstChar == 'T' || firstChar == 'G') {
            total += 4;
        }

        int remainder = total % 11;
        char[] lastChars = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};

        if (firstChar == 'S' || firstChar == 'T') {
            return nric.charAt(8) == lastChars[remainder];
        } else {
            return nric.charAt(8) == lastChars[remainder + 4];
        }
    }

    public static boolean isValidPassport(String passportNumber) {
        return PASSPORT_PATTERN.matcher(passportNumber).matches();
    }

    public static boolean isValidMobileNumber(String mnum){
        return MNUMBER_PATTERN.matcher(mnum).matches();
    }

    public static boolean isValidEmail(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static void CreateNewCustomerAccount() throws NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        Customer cust = new Customer();
        Database db = new Database();

        System.out.println("Please enter customer's full name: ");
        String name = "";
        while (true) {
            name = sc.nextLine().trim();
            if (name.matches("^[a-zA-Z ]+$")) {
                break;
            } else {
                System.out.print(ANSI_RED + "Invalid input. Please enter only alphabets and spaces: " + ANSI_RESET);
            }
        }
        cust.setfName(name.toUpperCase());

        int customerType;
        do {
            System.out.println("\nIs the customer \n[1] Singaporean/PR or \n[2] Foreigner?");
            while (!sc.hasNextInt()) {
                System.out.println(ANSI_RED
                        + "Invalid selection. Please enter [1] for Singaporean/PR or [2] for Foreigner:" + ANSI_RESET);
                sc.next();
            }
            customerType = sc.nextInt();
        } while (customerType != 1 && customerType != 2);

        if (customerType == 1) {
            String nric;
            boolean isValid = false;

            while (!isValid) {
                System.out.println("\nPlease enter the customer's NRIC: ");
                nric = sc.next().toUpperCase();

                if (Customer.isValidNRIC(nric)) {
                    System.out.println(nric + " is a valid NRIC number.");
                    cust.setNric(nric);
                    isValid = true;
                } else {
                    System.out.println(ANSI_RED+nric + " is not a valid NRIC number. Please enter a valid NRIC."+ANSI_RESET);
                }
            }
        } else {
            String passNum;
            boolean isValid = false;
            while (!isValid) {
                System.out.println("\nPlease enter the customer's passport number: ");
                passNum = sc.next().toUpperCase();

                if (Customer.isValidPassport(passNum)) {
                    System.out.println(passNum + " is a valid passport number.");
                    cust.setPnumber(passNum);
                    isValid = true;
                } else {
                    System.out
                            .println(ANSI_RED+passNum + " is not a valid passport number. Please enter a valid passport number"+ANSI_RESET);
                }
            }
        }
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's country: ");
        cust.setCountry(sc.nextLine().toUpperCase());
        System.out.println("\nPlease enter the customer's gender: ");
        cust.setGender(sc.next().toUpperCase());
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's date of birth (YYYY-MM-DD): ");
        String dobRegex = "\\d{4}-\\d{2}-\\d{2}";
        String dob = sc.next();
        while (!dob.matches(dobRegex)) {
            System.out.println(ANSI_RED
                    + "Invalid date format. Please enter the customer's date of birth in this format YYYY-MM-DD: "
                    + ANSI_RESET);
            dob = sc.next();
        }
        cust.setDob(dob);
        boolean isValid = false;
        while (!isValid) {
            System.out.println("\nPlease enter the customer's mobile number: ");
            cust.setmNumber(sc.next());
            if (Customer.isValidMobileNumber(cust.getmNumber())) {
                System.out.println("Valid mobile number");
                isValid = true;
            } else {
                System.out.println(ANSI_RED+"Invalid mobile number, please enter a valid mobile number."+ANSI_RESET);
            }
        }
        isValid = false;
        while (!isValid) {
            System.out.println("\nPlease enter the customer's email: ");
            cust.setEmail(sc.next());
            if (Customer.isValidEmail(cust.getEmail())) {
                System.out.println("Valid email");
                isValid = true;
            } else {
                System.out.println(ANSI_RED+"Invalid email address, please enter a valid email address."+ANSI_RESET);
            }
        }
        // consume the end of line at the end of sc.next()
        sc.nextLine();
        System.out.println("\nPlease enter the customer's home address: ");
        cust.setAddress(sc.nextLine());

        // Generate timestamp for account creation
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDateTime = sdf.format(dt);

        // Check if customer exist in the database
        try {
            ResultSet resultSet;
            if (customerType == 1) {
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE nric=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getNric());
                ;
                resultSet = db.executeQuery(pStatement);

            } else {
                String query = "SELECT user_id,nric,passport_number FROM customer WHERE passport_number=?";
                PreparedStatement pStatement = db.getConnection().prepareStatement(query);
                pStatement.setString(1, cust.getPnumber());
                ;
                resultSet = db.executeQuery(pStatement);
            }
            if (resultSet.next()) {
                System.out.println(ANSI_RED+"\nThis account already exist!\n"+ANSI_RESET);
                Teller.showTellerMenu();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL driver not found.");
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }

        // Insert customer account into DB
        try {
            String query = "INSERT INTO customer(full_name, nric, passport_number, country, gender, dob, mobile_number, address, email, created_date,deactivation_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pStatement = db.getConnection().prepareStatement(query);
            pStatement.setString(1, cust.getfName());
            pStatement.setString(2, cust.getNric());
            pStatement.setString(3, cust.getPnumber());
            pStatement.setString(4, cust.getCountry());
            pStatement.setString(5, cust.getGender());
            pStatement.setString(6, cust.getDob());
            pStatement.setString(7, cust.getmNumber());
            pStatement.setString(8, cust.getAddress());
            pStatement.setString(9, cust.getEmail());
            pStatement.setString(10, creationDateTime);
            pStatement.setNull(11, Types.DATE);
            if (db.executeUpdate(pStatement) > 0) {
                System.out.println(ANSI_CYAN+"Customer account has been created successfully!"+ANSI_RESET);
                ResultSet resultSet;
                if (customerType == 1) {
                    System.out.println("NRIC route");
                    resultSet = db.executeQuery(
                            "SELECT user_id,nric,passport_number FROM customer WHERE nric='" + cust.getNric() + "'");
                } else {
                    System.out.println("Pnumber route");
                    resultSet = db
                            .executeQuery("SELECT user_id,nric,passport_number FROM customer WHERE passport_number='"
                                    + cust.getPnumber() + "'");
                }
                String userId = null;
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                    Account.createAccount(userId);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error executing SQL query. Details: \n" + e.getMessage());
        }
    }

    public static void CustomerMode() throws Exception {
        Console console = System.console();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Your Account Number: ");
        String accNum = sc.next();
        System.out.println("Please Enter Your Pin: ");
        String pin = sc.nextLine();
        if (System.console() != null) {
            char[] enteredPin = console.readPassword();
            pin = String.valueOf(enteredPin);
        }
        System.out.println();

        Account.validator(accNum, pin);
    }

    public static void showCustomerMenu(String accNum, String pin, String userId) throws Exception {
        Scanner sc = new Scanner(System.in);
        Database db = new Database();
        String name = null;
        String SAaccNo = null;
        String CAaccNo = null;
        double savingsBalance = 0;
        double checkingBalance = 0;

        name = Account.getProfile(userId, pin);
        SAaccNo = Account.getAccounts(userId, "Savings Account", pin);
        CAaccNo = Account.getAccounts(userId, "Current Account", pin);
        savingsBalance = Account.getBalanceFromAccount(SAaccNo, pin);
        checkingBalance = Account.getBalanceFromAccount(CAaccNo, pin);

        String choice;
        double totalBalance = checkingBalance + savingsBalance;
        double deposit_amount = 0;
        double transfer_amount = 0;
        double withdrawal_amount = 0; // Not sure if we should impose a min withdraw amount

        System.out.println(ANSI_YELLOW + "Hello " + name + ",");
        System.out.println("As of " + new java.util.Date() + "," + ANSI_RESET);
        System.out.println("  -----------------------------");
        System.out.printf("  | Saving Balance: $%.2f    |\n", savingsBalance);
        System.out.println("  -----------------------------");
        System.out.printf("  | Checking Balance: $%.2f |\n", checkingBalance);
        System.out.println("  -----------------------------");
        System.out.printf("  | Total Balance: $%.2f    |\n", totalBalance);
        System.out.println("  -----------------------------\n");
        System.out.println(
                ANSI_YELLOW + "What would you like to do today? Please enter your choice" + ANSI_RESET);
        while (true) {
            String leftMenuFormat = "| %-10s: %-15s |%n";
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 1]", "Check Balance");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 2]", "Fund Transfer");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 3]", "Cash Withdraw");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 4]", "All Accounts");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 5]", "Deposit Cash");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 6]", "Other Services");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(
                    "-------------------------------%n");
            System.out.format(leftMenuFormat, "[press 7]", "Exit or Quit");
            System.out.format(
                    "-------------------------------%n");
            System.out.println();

            choice = sc.next();
            switch (choice) {
                case "1":
                    showBalance(SAaccNo, CAaccNo, pin);
                    break;

                case "2":
                    String action = "transfer";
                    List<String> accList = Account.selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accTrfNo = accList.get(0);
                    String accRrfNo = null;
                    String accTypeName = accList.get(1);

                    List<Object> trfList = Account.retrieveAcc(accTrfNo, pin, accTypeName);
                    boolean trfpassedTest = (boolean) trfList.get(0);
                    double trfaccReBalance = 0;
                    String trfName = null;

                    List<Object> rrfList = new ArrayList<Object>();
                    boolean rrfpassedTest = false;
                    double rrfaccReBalance = 0;
                    String rrfName = null;

                    boolean checker = true;

                    if (trfpassedTest) {
                        trfaccReBalance = (double) trfList.get(1);
                        trfName = (String) trfList.get(2);
                        while (!rrfpassedTest) {
                            System.out.print(
                                    "\nEnter Account No. to transfer to: #");
                            accRrfNo = sc.next();
                            rrfList = Account.retrieveAcc(accRrfNo, pin, "***");
                            rrfpassedTest = (boolean) rrfList.get(0);

                            if (accRrfNo.equals(accTrfNo)) {
                                System.out.println(
                                        ANSI_RED + "You cannot transfer to the same account.\n" + ANSI_RESET);
                                checker = false;
                            }
                        }
                        rrfaccReBalance = (double) rrfList.get(1);
                        rrfName = (String) rrfList.get(2);

                        String actionStatementTo = "TRANSFER TO ACC NO." + accRrfNo;
                        String actionStatementFrom = "TRANSFER FROM ACC NO." + accTrfNo;

                        // Get current available balance
                        double currentBalanceToTransfer = Account.getBalanceFromAccount(accTrfNo, pin);
                        while (checker) {
                            checker = false;
                            System.out.print("\nEnter amount to transfer: $");
                            transfer_amount = sc.nextDouble();

                            if (transfer_amount > currentBalanceToTransfer) {
                                System.out
                                        .println(ANSI_RED + "Your balance is insufficient, please re-enter amount."
                                                + ANSI_RESET);
                                checker = true;
                            } else if (transfer_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Transfer amount have to be bigger than $0.00"
                                                + ANSI_RESET);
                                checker = true;
                            } else if (transfer_amount > 1000000) {
                                System.out
                                        .println(ANSI_RED + "Exceeded transfer amount of $1,000,000."
                                                + ANSI_RESET);
                                checker = true;
                            } else {
                                deposit(sc, userId, pin, accRrfNo, accTypeName, action,
                                        actionStatementFrom,
                                        transfer_amount,
                                        rrfaccReBalance, rrfName, name);
                                withdraw(sc, userId, pin, accTrfNo, accTypeName, action,
                                                actionStatementTo,
                                                transfer_amount,
                                                trfaccReBalance, trfName, name);
                            }
                        }
                    }
                    break;

                case "3":
                    action = "withdraw";
                    String actionStatement = "ATM WITHDRAWAL";
                    accList = Account.selectAccount(sc, CAaccNo, SAaccNo, action);
                    String accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    List<Object> rList = Account.retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = Account.getBalanceFromAccount(accReNo, pin);
                        System.out.print("\nEnter amount to withdraw: $");
                        withdrawal_amount = sc.nextDouble();
                        while (withdrawal_amount > currentBalanceToWithdraw) {
                            System.out.println(
                                    ANSI_RED + "Your balance is insufficient, please re-enter amount.\n"
                                            + ANSI_RESET);
                            System.out.print("Enter amount to withdraw: $");
                            withdrawal_amount = sc.nextDouble();
                        }
                        checker = true;
                        while (checker) {
                            checker = false;
                            if (withdrawal_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Withdrawal amount have to be bigger than $0.00"
                                                + ANSI_RESET);
                                System.out.print("\nEnter amount to withdraw: $");
                                withdrawal_amount = sc.nextDouble();
                                checker = true;
                            }
                        }
                        withdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                        withdrawal_amount,
                                        accReBalance, trfName,name);
                    }
                    break;
                case "4":
                    showAllAccounts(SAaccNo, CAaccNo, pin);
                    break;
                case "5":
                    action = "deposit";
                    actionStatement = "ATM DEPOSIT";
                    accList = Account.selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    rList = Account.retrieveAcc(accReNo, pin, accTypeName);
                    passedTest = (boolean) rList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) rList.get(1);
                        trfName = (String) rList.get(2);
                        System.out.print("\nEnter amount to deposit: $");
                        deposit_amount = sc.nextDouble();
                        checker = true;
                        while (checker) {
                            checker = false;
                            if (deposit_amount <= 0) {
                                System.out
                                        .println(ANSI_RED + "Deposit amount have to be bigger than $0.00\n"
                                                + ANSI_RESET);
                                System.out.print("\nEnter amount to deposit: $");
                                deposit_amount = sc.nextDouble();
                                checker = true;
                            }
                        }
                        deposit(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                        deposit_amount,
                                        accReBalance,
                                        trfName,name);
                    }
                    break;
                case "6":
                    showCustomerSubMenu(accNum, pin, userId, CAaccNo, SAaccNo, name);
                    break;
                case "7":
                    System.out.println("\nThank you for banking with SIT ATM.\n");
                    System.exit(0);
            }
        }
    }

    public static void showCustomerSubMenu(String accNum, String pin, String userId, String CAaccNo, String SAaccNo, String name)
            throws Exception {
        Database db = new Database();
        System.out.println("\n" + ANSI_YELLOW + "Other Services" + ANSI_RESET);
        String leftSubMenuFormat = "| %-10s: %-28s |%n";
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 1]", "Change Pin Number");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 2]", "Update Personal Particulars");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 3]", "Pay Bills");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 4]", "Apply Bank Loan");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 5]", "Show Latest Transactions");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 6]", "Go back");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(
                "--------------------------------------------%n");
        System.out.format(leftSubMenuFormat, "[press 7]", "Terminate / Quit");
        System.out.format(
                "--------------------------------------------%n");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        String xchoice = sc.next();
        switch (xchoice) {
            case "1":
                changePin(sc, userId);
                break;

            case "2":
                System.out.print("Chose Option 2, WIP");
                break;

            case "3":
                // when approved, set repayment date by 1 month from approval
                String action = "repay loan";
                String actionStatement = "LOAN REPAYMENT";
                List<String> accList = Account.selectAccount(sc, CAaccNo, SAaccNo, action);
                String accReNo = accList.get(0);
                String accTypeName = accList.get(1);

                int loan_id = 0;
                double principleAmt = 0;
                double interestRate = 0;
                int duration = 0;
                double debt = 0;
                double currentDebt = 0;
                java.sql.Date date_created = null;
                java.sql.Date repayment_date = null;
                String status = null;
                int count = 0;

                ResultSet checkLoanIP = db.executeQuery(
                        "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN loan ON account.account_number = loan.account_number WHERE account.account_number = "
                                + accReNo + " AND loan.status = 'APPROVED'"); //
                while (checkLoanIP.next()) {
                    loan_id = checkLoanIP.getInt("loan_id");
                    principleAmt = checkLoanIP.getDouble("principle_amt");
                    interestRate = checkLoanIP.getDouble("interest_rate");
                    duration = checkLoanIP.getInt("duration");
                    debt = checkLoanIP.getDouble("debt");
                    date_created = checkLoanIP.getDate("date_created");
                    repayment_date = checkLoanIP.getDate("repayment_date");
                    status = checkLoanIP.getString("status");
                    count++;
                }
                if (count <= 0) {
                    System.out.printf("\n%s%s account does not have an ongoing approved loan.%s\n\n",
                            ANSI_RED, accTypeName, ANSI_RESET);
                } else {
                    currentDebt = debt;
                    System.out.printf(
                            "\n%sLoan retrieved.%s\nPrinciple Amount:%.2f\nCurrent Interest Rate:%.2f\nLoan Tenor Left:%d\nNext Repayment Date:%s\nThis Month's Repayment:%.2f\nTotal Debt:%.2f\n\n",
                            ANSI_YELLOW, ANSI_RESET, principleAmt, interestRate, duration, repayment_date,
                            currentDebt / duration, currentDebt);

                    java.sql.Date todayDate = java.sql.Date.valueOf(java.time.LocalDate.now());
                    while (todayDate.after(repayment_date)) {
                        // Penatly for late repayment, recal debt
                        interestRate += 5.00;
                        if (duration > 1) {
                            repayment_date = java.sql.Date
                                    .valueOf(repayment_date.toLocalDate().plusMonths(1));
                            duration -= 1;
                        } else {
                            duration = 1;
                            status = "OVERDUED";
                        }
                        currentDebt *= ((100 + interestRate) / 100);
                        System.out.printf(
                                "\n%sLoan dued.%s\nPrinciple Amount:%.2f\nCurrent Interest Rate:%.2f\nLoan Tenor Left:%d\nNext Repayment Date:%s\nThis Month's Repayment:%.2f\nDebt:%.2f\n\n",
                                ANSI_YELLOW, ANSI_RESET, principleAmt, interestRate, duration,
                                repayment_date,
                                currentDebt / duration, currentDebt);
                    }

                    action = "repay loan";
                    actionStatement = "LOAN REPAYMENT";
                    accList = Account.selectAccount(sc, CAaccNo, SAaccNo, action);
                    accReNo = accList.get(0);
                    accTypeName = accList.get(1);

                    double currentMonthDebt = currentDebt / duration;

                    List<Object> payList = Account.retrieveAcc(accReNo, pin, accTypeName);
                    boolean passedTest = (boolean) payList.get(0);
                    if (passedTest) {
                        double accReBalance = (double) payList.get(1);
                        String trfName = (String) payList.get(2);
                        // Get current avaliable balance
                        double currentBalanceToWithdraw = Account.getBalanceFromAccount(accReNo, pin);
                        System.out.printf("\nAmount to repay: $%.2f", currentMonthDebt);
                        if (currentMonthDebt > currentBalanceToWithdraw) {
                            System.out.println(
                                    ANSI_RED + "Your balance is insufficient, operation cancelled.\n"
                                            + ANSI_RESET);
                            return;
                        } else {
                            DecimalFormat df = new DecimalFormat("#.##");
                            debt = Double.valueOf(df.format(debt));

                            // if debt is fully paid for this month, set month to next and duration -1
                            repayment_date = java.sql.Date
                                    .valueOf(repayment_date.toLocalDate().plusMonths(1));
                            if (duration > 1) {
                                duration -= 1;
                            } else {
                                status = "CLOSED";
                                duration -= 1;
                            }
                            debt -= currentMonthDebt;
                            withdraw(sc, userId, pin, accReNo, accTypeName, action, actionStatement,
                                            currentMonthDebt,
                                            accReBalance, trfName,name);
                            String updateLoanQuery = "UPDATE `loan` SET `principle_amt`=?,`interest_rate`=?,`duration`=?,`debt`=?,`repayment_date`=?,`status`=? WHERE `loan_id` = ?";
                            PreparedStatement updateLoanStatement = db.getConnection()
                                    .prepareStatement(updateLoanQuery);
                            updateLoanStatement.setDouble(1, principleAmt);
                            updateLoanStatement.setDouble(2, interestRate);
                            updateLoanStatement.setInt(3, duration);
                            updateLoanStatement.setDouble(4, debt);
                            updateLoanStatement.setDate(5, repayment_date);
                            updateLoanStatement.setString(6, status);
                            updateLoanStatement.setInt(7, loan_id);
                            int rowsLoanAffected = updateLoanStatement.executeUpdate();
                            if (rowsLoanAffected > 0) {
                                System.out.println("\n\nLoan repayment has successfully proccessed.");
                            }
                        }
                    }
                }
                break;
            case "4":
                String accLoanNo = null;
                String accLoanTypeName = null;
                double intRate = 3.88;
                double proRate = 1.00;
                double lateRate = 5;
                System.out.printf("\nTotal Limit: " + "\n");
                System.out.printf("Available Limit: " + "\n");
                System.out.printf("Interst Rate: %.2f" + "%% p.a\n", intRate);
                System.out.printf("Processing Fee : %.2f" + "%%\n\n", proRate);
                System.out.printf("Late Repayement Fee : %.2f" + "%%\n\n", lateRate);

                action = "loan application";
                actionStatement = "LOAN APPLICATION";
                accList = Account.selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
                accLoanNo = accList.get(0);
                accLoanTypeName = accList.get(1);

                boolean checker = true;
                double loan_amount = 0;
                while (checker) {
                    checker = false;
                    System.out.print("\nRequested Loan Amount: $");
                    loan_amount = sc.nextDouble();
                    if (loan_amount <= 0) {
                        System.out
                                .println(ANSI_RED
                                        + "\nLoan amount have to be bigger than $0.00\n"
                                        + ANSI_RESET);
                        checker = true;
                    }
                }

                Loan.applyLoan(sc, userId, pin, accLoanNo, accLoanTypeName, action, actionStatement,
                        loan_amount,
                        intRate, proRate, lateRate);
                break;

            case "5":
                actionStatement = "PRINT LATEST TRANSACTIONS";
                accList = Account.selectAccount(sc, CAaccNo, SAaccNo, actionStatement);
                String accStatNo = accList.get(0);
                String accStatType = accList.get(1);
                List<Object> pList = Account.retrieveAcc(accStatNo, pin, accStatType);
                boolean passedTest = (boolean) pList.get(0);
                if (passedTest) {
                    printLatestTransactions(accStatNo, pin, accStatType);
                }
                break;

            case "6":
                showCustomerMenu(accNum, pin, userId);
                break;

            case "7":
                System.out.println("\nThank you for banking with SIT ATM.\n");
                System.exit(0);
        }
    }
    
    public static void showBalance(String SAaccNo, String CAaccNo, String pin)
            throws ClassNotFoundException, SQLException {
        double savingsBalance = Account.getBalanceFromAccount(SAaccNo, pin);
        double checkingBalance = Account.getBalanceFromAccount(CAaccNo, pin);
        double totalBalance = checkingBalance + savingsBalance;
        System.out.printf("\n%sYour current balance is $%.2f%s\n",
                Customer.ANSI_YELLOW, totalBalance,
                Customer.ANSI_RESET);
        System.out.println(Customer.ANSI_YELLOW + "Last Updated: " + new java.util.Date() + Customer.ANSI_RESET);

        String accAlignFormat = "| %-20s | %-12.2f |%n";
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.format(accAlignFormat, "Total Balance", totalBalance);
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.println();
    }

    public static void showAllAccounts(String SAaccNo, String CAaccNo, String pin)
            throws ClassNotFoundException, SQLException {
        double savingsBalance = Account.getBalanceFromAccount(SAaccNo, pin);
        double checkingBalance = Account.getBalanceFromAccount(CAaccNo, pin);
        double totalBalance = checkingBalance + savingsBalance;
        System.out.println(Customer.ANSI_YELLOW + "\nAs of " + new java.util.Date() + "," + Customer.ANSI_RESET);
        System.out.println("Here are your list of accounts: ");
        String accAlignFormat = "| %-20s | %-12.2f |%n";
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.format(accAlignFormat, "Saving Balance", savingsBalance);
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.format(accAlignFormat, "Checking Balance", checkingBalance);
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.format(accAlignFormat, "Total Balance", totalBalance);
        System.out.format(
                "+----------------------+--------------+%n");
        System.out.println();
    }

    public static void deposit(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double deposit_amount, double accReBalance, String trfName, String name) throws ClassNotFoundException, SQLException {
        boolean persistent = true;
        String pinOp = "0";
        Database db = new Database();
        while (persistent) {
            if (action == "deposit" || action == "withdraw") {
                System.out.println(
                        Customer.ANSI_CYAN + "\nProceed with " + action + "?" + Customer.ANSI_RESET
                                + "\n[1] Yes\n[2] No" + Customer.ANSI_RESET);
                pinOp = sc.next();
            } else {
                pinOp = "1";
            }
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        accReBalance = accReBalance + deposit_amount;
                        String depositAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                        PreparedStatement depositAmountBalance = db.getConnection().prepareStatement(depositAmountBalanceQuery, Statement.RETURN_GENERATED_KEYS);
                        depositAmountBalance.setString(1, accReNo);
                        depositAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        depositAmountBalance.setString(3, actionStatement);
                        depositAmountBalance.setInt(4, 0);
                        depositAmountBalance.setDouble(5, 0);
                        depositAmountBalance.setDouble(6, deposit_amount);
                        depositAmountBalance.setDouble(7, accReBalance);
                        int rowsDptAffected = depositAmountBalance.executeUpdate();
                        if (rowsDptAffected <= 0) {
                            System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                    Customer.ANSI_RED, Customer.ANSI_RESET);
                            break;
                        } else {
                            try (ResultSet rs = depositAmountBalance.getGeneratedKeys()) {
                                if (rs.next()) {
                                    int transactionId = rs.getInt(1);
                                    if (action == "deposit" || action == "withdraw") {
                                        askReceipt(sc, accReNo, action, actionStatement,
                                                deposit_amount, accTypeName,
                                                accReBalance,name,transactionId);
                                    } else if (action == "transfer") {
                                        System.out.printf(
                                                "\n%sYou have successfully transferred $%.2f to Acc No. %s (%s) %s",
                                                Customer.ANSI_CYAN,
                                                deposit_amount,
                                                accReNo, trfName, Customer.ANSI_RESET);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error executing SQL query. Details: \n" +
                                e.getMessage());
                    }
                    break;
                case "2":
                    System.out.println(
                            Customer.ANSI_RED + "\nOperation cancelled.\n"
                                    + Customer.ANSI_RESET);
                    break;
                default:
                    System.out.println(
                            Customer.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + Customer.ANSI_RESET);
                    persistent = true;
            }
        }
    }

    public static void withdraw(Scanner sc, String userId, String pin, String accReNo, String accTypeName,
            String action, String actionStatement, double withdraw_amount, double accReBalance, String trfName, String name)
            throws ClassNotFoundException, SQLException {
        boolean persistent = true;
        String pinOp = "0";
        Database db = new Database();
        while (persistent) {
            if (action == "deposit" || action == "withdraw") {
                System.out.println(
                        Customer.ANSI_CYAN + "\nProceed with " + action + "?" + Customer.ANSI_RESET
                                + "\n[1] Yes\n[2] No" + Customer.ANSI_RESET);
                pinOp = sc.next();
            } else {
                pinOp = "1";
            }
            persistent = false;
            switch (pinOp) {
                case "1":
                    try {
                        accReBalance = accReBalance - withdraw_amount;
                        String depositAmountBalanceQuery = "INSERT INTO `transaction`(`account_number`, `date`, `transaction_details`, `chq_no`, `withdrawal_amt`, `deposit_amt`, `balance_amt`) VALUES (?,?,?,?,?,?,?);";
                        PreparedStatement depositAmountBalance = db.getConnection().prepareStatement(depositAmountBalanceQuery);
                        depositAmountBalance.setString(1, accReNo);
                        depositAmountBalance.setDate(2,
                                java.sql.Date.valueOf(java.time.LocalDate.now()));
                        depositAmountBalance.setString(3, actionStatement);
                        depositAmountBalance.setInt(4, 0);
                        depositAmountBalance.setDouble(5, withdraw_amount);
                        depositAmountBalance.setDouble(6, 0);
                        depositAmountBalance.setDouble(7, accReBalance);
                        int rowsDptAffected = depositAmountBalance.executeUpdate();
                        if (rowsDptAffected <= 0) {
                            System.out.printf("\n%sSQL Error. Please try again.%s\n\n",
                                    Customer.ANSI_RED, Customer.ANSI_RESET);
                            break;
                        } else {
                            try (ResultSet rs = depositAmountBalance.getGeneratedKeys()) {
                                if (rs.next()) {
                                    int transactionId = rs.getInt(1);
                                    if (action == "deposit" || action == "withdraw") {
                                        askReceipt(sc, accReNo, action, actionStatement,
                                                withdraw_amount, accTypeName,
                                                accReBalance,name,transactionId);
                                    } else if (action == "transfer") {
                                        System.out.printf(
                                                "%sand your current %s Account Balance is $%.2f.%s\n\n",
                                                Customer.ANSI_CYAN, accTypeName, accReBalance, Customer.ANSI_RESET);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error executing SQL query. Details: \n" +
                                e.getMessage());
                    }
                    break;
                case "2":
                    System.out.println(
                            Customer.ANSI_RED + "\nOperation cancelled.\n"
                                    + Customer.ANSI_RESET);
                    break;
                default:
                    System.out.println(
                            Customer.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + Customer.ANSI_RESET);
                    persistent = true;
            }
        }
    }

    public static void printLatestTransactions(String accStatNo, String pin, String accStatType) {
        String accStat = "";
        double accStatD = 0;
        double accStatW = 0;
        double accStatBalance = 0;
        int count = 0;
        Database db = new Database();
        System.out.println("\nShowing (5) latest transactions:");
        try {
            ResultSet StatRetreival = db.executeQuery(
                            "SELECT * FROM account JOIN customer ON account.user_id = customer.user_id LEFT JOIN transaction ON account.account_number = transaction.account_number WHERE account.account_number = "
                                    + accStatNo + " AND account.account_number = " + accStatNo
                                    + " ORDER BY transaction_id DESC LIMIT 5;"); //

            String leftAlignFormat = "| %-44s | %-12.2f | %-12.2f | %-12.2f |%n";
            System.out.format(
                    "+----------------------------------------------+--------------+--------------+--------------+%n");
            System.out.format(
                    "| Transactions                                 | Desposit     | Withdrawal   | Balance      |%n");
            System.out.format(
                    "+----------------------------------------------+--------------+--------------+--------------+%n");

            while (StatRetreival.next()) {
                accStat = StatRetreival.getString("transaction_details");
                accStatD = StatRetreival.getDouble("deposit_amt");
                accStatW = StatRetreival.getDouble("withdrawal_amt");
                accStatBalance = StatRetreival.getDouble("balance_amt");
                count++;
                System.out.format(leftAlignFormat, accStat, accStatD,
                        accStatW,
                        accStatBalance);
                System.out.format(
                        "+----------------------------------------------+--------------+--------------+--------------+%n");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println(
                    Customer.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                            + Customer.ANSI_RESET);
            return;
        }
    }

    public static void askReceipt(Scanner sc, String accReNo, String action, String actionStatement,
            double transaction_amount, String accTypeName,
            double accReBalance, String name, int transactionId) {
        boolean persistent = true;
        while (persistent) {
            System.out.println(
                    Customer.ANSI_CYAN + "\nPrint Receipt? " + Customer.ANSI_RESET
                            + "\n[1] Yes\n[2] No" + Customer.ANSI_RESET);
            String pinOp = sc.next();
            persistent = false;
            switch (pinOp) {
                case "1":
                    printReceipt(accReNo, action, actionStatement,
                            transaction_amount, accTypeName,
                            accReBalance, name, transactionId);
                    break;
                case "2":
                    noReceipt(action, transaction_amount, accTypeName, accReBalance);
                    break;
                default:
                    System.out.println(
                            Customer.ANSI_RED + "Invalid option, please re-enter.\n"
                                    + Customer.ANSI_RESET);
                    persistent = true;
            }
        }
    }

    public static void printReceipt(String accReNo, String action, String actionStatement, double transaction_amount,
            String accTypeName, double accReBalance, String name, int transactionId) {
        int option = 0;
        if (action.equals("ATM DEPOSIT")){
            option = 0;
        }else if (action.equals("ATM WITHDRAWAL")){
            option = 1;
        }
        String strReFormat = "| %-20s   %-12s |%n";
        String leftReFormat = "| %-20s   %-12.2f |%n";
        System.out.println();
        System.out.format(
                "+-------------------------------------+%n");
        System.out.format(
                "|               Receipt               |%n");
        System.out.format(
                "+-------------------------------------+%n");
        System.out.format(
                strReFormat, "Account No.",
                accReNo);
        System.out.format(
                "|                                     |%n");
        System.out.format(leftReFormat,
                actionStatement,
                transaction_amount);
        System.out.format(leftReFormat, "Account Balance",
                accReBalance);
        System.out.format(
                "+-------------------------------------+%n");
        System.out.println();
        receiptPrinter.printReceipt(name,java.sql.Date.valueOf(java.time.LocalDate.now()),accReNo,transactionId,transaction_amount,accReBalance,option);
        noReceipt(action, transaction_amount, accTypeName, accReBalance);
    }

    public static void noReceipt(String action, double transaction_amount, String accTypeName, double accReBalance) {
        System.out.printf(
                "\n%sYou have are successfully %s $%.2f and your current %s Account Balance is $%.2f.%s\n\n",
                Customer.ANSI_CYAN,
                action,
                transaction_amount,
                accTypeName,
                accReBalance, Customer.ANSI_RESET);
    }

    public static void changePin(Scanner sc, String userId) throws NoSuchAlgorithmException{
        Console console = System.console();
        boolean persistent = true;
        Database db = new Database();
        while (persistent) {
            persistent = false;
            System.out.print("\nEnter new 6 Digit Pin: ");
            String newPin = sc.nextLine();
            if (System.console() != null) {
                char[] enteredPin = console.readPassword();
                newPin = String.valueOf(enteredPin);
            }

            if (newPin.matches("\\d{6}")) {
                // Hash customer's pin with Salt and Pepper
                String[] hashAlgo;
                try {
                    hashAlgo = PinHash.hashPin(newPin);
                    String newHashedPin = hashAlgo[1];
                    String newSalt = hashAlgo[0];
                    // Debug
                    System.out.println("Random Salt: " + hashAlgo[1]);
                    System.out.println("Hashed Password: " + hashAlgo[0]);
                    persistent = true;
                    while (persistent) {
                        System.out.println(
                                Customer.ANSI_CYAN + "\nProceed with pin change? " + Customer.ANSI_RESET
                                        + "\n[1] Yes\n[2] No" + Customer.ANSI_RESET);
                        String pinOp = sc.next();
                        persistent = false;
                        switch (pinOp) {
                            case "1":
                                try {
                                    String npQuery = "UPDATE account SET pin = ?, salt = ? WHERE user_id = ?";
                                    PreparedStatement npStatement = db.getConnection().prepareStatement(npQuery);
                                    npStatement.setString(1, newHashedPin);
                                    npStatement.setString(2, newSalt);
                                    npStatement.setString(3, userId);
                                    npStatement.executeUpdate();
                                    System.out
                                            .println(
                                                    Customer.ANSI_CYAN
                                                            + "\nYou have successfully changed your pin.\n"
                                                            + Customer.ANSI_RESET);
                                } catch (SQLException e) {
                                    // TODO: handle exception
                                    System.out.println(
                                            Customer.ANSI_RED + "\nAn SQL error has occurred, please try later.\n"
                                                    + Customer.ANSI_RESET);
                                }
                                break;
                            case "2":
                                System.out.println(
                                        Customer.ANSI_RED + "\nOperation cancelled.\n"
                                                + Customer.ANSI_RESET);
                                break;
                            default:
                                System.out.println(
                                        Customer.ANSI_RED + "Invalid option, please re-enter.\n"
                                                + Customer.ANSI_RESET);
                                persistent = true;
                        }
                    }
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    System.out.println(
                            Customer.ANSI_RED + "\nAn error has occurred, please try again.\n"
                                    + Customer.ANSI_RESET);
                }
                break;
            } else {
                System.out.println(
                        Customer.ANSI_RED + "\nInvalid input. " + Customer.ANSI_RESET
                                + "Please enter a 6 digit pin: ");
                persistent = true;
            }
        }
    }
}