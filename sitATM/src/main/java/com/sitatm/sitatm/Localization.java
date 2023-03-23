package com.sitatm.sitatm;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    ATM atm = new ATM();
    public ResourceBundle bundle;
    public Localization(){
        Locale locale = new Locale("en");
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);
    }
    public void setLocale(String fxmlFile,String lang) throws IOException {
        Locale locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("labelText", locale);
        //FXMLLoader fxmlLoader = new FXMLLoader();
        atm.changeScene(fxmlFile, bundle);
    }

    public ResourceBundle getBundle(){
        return bundle;
    }
}