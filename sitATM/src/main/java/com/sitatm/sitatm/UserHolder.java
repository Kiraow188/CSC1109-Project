package com.sitatm.sitatm;

// This Java file is a Singleton class
public class UserHolder {
    // Initialize Customer Object
    private Customer customer;
    private Database database;
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

    public void setDatabase(Database d) { this.database = d; }
    public Database  getDatabase() { return this.database; }
}
