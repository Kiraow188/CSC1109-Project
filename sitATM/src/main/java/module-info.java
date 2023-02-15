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

    opens com.sitatm.sitatm to javafx.fxml;
    exports com.sitatm.sitatm;
}