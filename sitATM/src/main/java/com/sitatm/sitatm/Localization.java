package com.sitatm.sitatm;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private ATM atm = new ATM();
    private ResourceBundle bundle;
    private Locale defaultLocale = Locale.getDefault();

    public Localization(){
        bundle = ResourceBundle.getBundle("labelText", defaultLocale);
    }

    public void setLocale(String fxmlFile, String lang) throws IOException {
        Locale locale;
        if (lang == null || lang.isEmpty()) {
            locale = defaultLocale;
        } else {
            locale = new Locale(lang);
            defaultLocale = locale;
            System.out.println("Locale set to: "+lang);
        }
        bundle = ResourceBundle.getBundle("labelText", locale);
        atm.changeScene(fxmlFile,bundle);
    }

    public ResourceBundle getLocale() {
        return ResourceBundle.getBundle("labelText", defaultLocale);
    }

    public void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
        bundle = ResourceBundle.getBundle("labelText", defaultLocale);
    }
}
