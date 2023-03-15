package com.sitatm.sitatm;

public class loan {
    static final private double maximumLoanAmount = 30000.0;

    public static boolean approveLoan(double requestedAmount){
        return !(requestedAmount > maximumLoanAmount);
    }
}
