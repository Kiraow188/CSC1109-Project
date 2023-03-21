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



public class ATM extends Application {
    private static Stage stg;
    @Override
    public void start(Stage primaryStage) throws IOException {
        //MySQL Connection
        Database db = new Database();

        try {
            ResultSet resultSet = db.executeQuery("SELECT COUNT(*) FROM transaction");
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
                System.out.println(resultSet.getString(1));
            }
            db.closeConnection();
        }catch (SQLException e) {
            System.err.println("Error executing SQL query. Details: " + e.getMessage());
        }

        stg = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("atm-login-view.fxml"));
        primaryStage.setTitle("SIT ATM");
        primaryStage.getIcons().add(new Image(ATM.class.getResourceAsStream("/icon.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        /**
        // create a new FXMLLoader and load the FXML file "atm-view.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(ATM.class.getResource("atm-view.fxml"));
        // create a new Scene using the loaded FXML file and set it to the stage
        Scene cardScene = new Scene(fxmlLoader.load());
        // set the title of the stage to "SIT ATM"
        primaryStage.setTitle("SIT ATM");
        // load the icon.png image from the resources folder and add it to the stage
        primaryStage.getIcons().add(new Image(ATM.class.getResourceAsStream("/icon.png")));
        // set the stage to be non-resizable
        primaryStage.setResizable(false);
        // set the scene to the stage and show the stage
        primaryStage.setScene(cardScene);
        primaryStage.show();
         **/
    }
    public void changeScene(String fxml) throws IOException{
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
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