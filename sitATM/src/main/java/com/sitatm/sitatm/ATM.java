package com.sitatm.sitatm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class ATM extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // create a new FXMLLoader and load the FXML file "atm-view.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(ATM.class.getResource("atm-view.fxml"));
        // create a new Scene using the loaded FXML file and set it to the stage
        Scene scene = new Scene(fxmlLoader.load());
        // set the title of the stage to "SIT ATM"
        stage.setTitle("SIT ATM");
        // load the icon.png image from the resources folder and add it to the stage
        stage.getIcons().add(new Image(ATM.class.getResourceAsStream("/icon.png")));
        // set the stage to be non-resizable
        stage.setResizable(false);
        // set the scene to the stage and show the stage
        stage.setScene(scene);
        stage.show();
    }

    /**
    The main entry point for the JavaFX application.
    This method launches the application and starts the JavaFX runtime.
    */
    public static void main(String[] args) {
        launch();
    }
}