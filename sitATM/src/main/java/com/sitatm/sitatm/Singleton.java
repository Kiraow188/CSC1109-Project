package com.sitatm.sitatm;

// This Java file is a Singleton class
public class Singleton {
    // Initialize Customer Object
    private Customer customer = new Customer();
    private Localization localization;
    private Database database;
    private Account account;
    // Initialize Singleton instance
    private final static Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() {return INSTANCE;}
    public void setUser(Customer c) {
        this.customer = c;
    }
    public Customer getUser() {return this.customer; }
    public void setLocalization(Localization l){ this.localization = l; }
    public Localization getLocalization() { return this.localization; }
    public void setDatabase(Database db) { this.database = db; }
    public Database getDatabase() { return this.database; }
    public void setAccount(Account a) { this.account = a; }
    public Account getAccount() { return this.account; }
}
