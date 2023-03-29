module com.sitatm.sitatm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires firebase.admin;
    requires com.google.auth;
    requires java.sql;
    requires mysql.connector.java;
    requires activation;
    requires javax.mail;
    requires itextpdf;

    opens com.sitatm.sitatm to javafx.fxml;
    exports com.sitatm.sitatm;
    exports com.sitatm.sitatm.CLI;
    opens com.sitatm.sitatm.CLI to javafx.fxml;
}