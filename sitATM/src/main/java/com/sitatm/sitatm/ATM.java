package com.sitatm.sitatm;

// JavaFX libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// Google Firebase libraries
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;


// Just java stuff
import java.io.FileInputStream;
import java.io.IOException;

public class ATM extends Application {

    private Firestore db;
    private FirebaseAuth auth;

    @Override
    public void start(Stage stage) throws IOException {

        // Open the ServiceAccountKey.json file for readin                         g
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/ServiceAccountKey.json");
        // Obtains GoogleCredentials from the service account input stream.
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        // Creates FirebaseOptions object and sets it to use the credentials obtained from the service account.
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        // Initializes the Firebase app with the specified options.
        FirebaseApp.initializeApp(options);
        // Obtains an instance of Firestore database.
        db = FirestoreClient.getFirestore();
        // Obtain an instance of Firebase Authentication
        auth = FirebaseAuth.getInstance();


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