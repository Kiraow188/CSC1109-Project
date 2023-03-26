package com.sitatm.sitatm;

// JavaFX libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// Just java stuff
import java.io.IOException;

// MySQL
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;


public class ATM extends Application {
    private static Stage stg;
    UserHolder holder = UserHolder.getInstance();
    @Override
    public void start(Stage primaryStage) throws IOException {
        //MySQL Connection
        Database db = new Database();
        holder.setDatabase(db);
        try {
            ResultSet resultSet = db.executeQuery("SELECT COUNT(*) FROM transaction");
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
                System.out.println(resultSet.getString(1));
            }
            //db.closeConnection();
        }catch (SQLException e) {
            System.err.println("Error executing SQL query. Details: " + e.getMessage());
        }

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