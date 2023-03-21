package com.sitatm.sitatm;

public class UserHolder {
    private Customer customer;
    private final static UserHolder INSTANCE = new UserHolder();

    private UserHolder() {}

    public static UserHolder getInstance() {
        return INSTANCE;
    }

    public void setUser(Customer c) {
        this.customer = c;
    }

    public Customer getUser() {
        return this.customer;
    }
}
