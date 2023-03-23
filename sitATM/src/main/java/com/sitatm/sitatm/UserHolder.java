package com.sitatm.sitatm;

// This Java file is a Singleton class
public class UserHolder {
    // Initialize Customer Object
    private Customer customer;
    private Localization localization;
    // Initialize Singleton instance
    private final static UserHolder INSTANCE = new UserHolder();

    private UserHolder() {}

    public static UserHolder getInstance() {return INSTANCE;}

    public void setUser(Customer c) {
        this.customer = c;
    }

    public Customer getUser() {
        return this.customer;
    }

    public void setLocalization(Localization l){ this.localization = l; }
    public Localization getLocalization() { return this.localization; }
}
