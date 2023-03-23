package com.sitatm.sitatm;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    ATM atm = new ATM();
    public ResourceBundle bundle;
    public Localization(){
        Locale locale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);
    }
    public void setLocale(String fxmlFile,String lang) throws IOException {
        Locale locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("labelText", locale);
        atm.changeScene(fxmlFile, bundle);
    }
    public ResourceBundle getLocale() {
        Locale locale = new Locale("en");
        return ResourceBundle.getBundle("labelText", locale);
    }
}
