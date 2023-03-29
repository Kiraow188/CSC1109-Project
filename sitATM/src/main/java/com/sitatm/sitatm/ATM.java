package com.sitatm.sitatm;

// JavaFX libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class ATM extends Application {
    private static Stage stg;
    Singleton holder = Singleton.getInstance();
    @Override
    public void start(Stage primaryStage) throws IOException {
        //Setup MySQL Connection
        Database db = new Database();
        holder.setDatabase(db);

        stg = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("atm-login-view.fxml"));
        primaryStage.setTitle("SIT ATM");
        primaryStage.getIcons().add(new Image(ATM.class.getResourceAsStream("/img/icon.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public void changeScene(String fxml) throws IOException{
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        Scene newScene = new Scene(pane);
        stg.setScene(newScene);
    }

    public void changeScene(String fxml, ResourceBundle bundle) throws IOException{
        Parent pane = FXMLLoader.load(getClass().getResource(fxml), bundle);
        Scene newScene = new Scene(pane);
        stg.setScene(newScene);
    }

    /**
    The main entry point for the JavaFX application.
    This method launches the application and starts the JavaFX runtime.
    */
    public static void main(String[] args) {
        launch();
    }
}