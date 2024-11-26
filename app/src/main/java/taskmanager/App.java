package taskmanager;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import taskmanager.services.SessionManager;
import taskmanager.utils.CommonUtil;
import taskmanager.utils.JwtUtil;

public class App extends Application {

    private static Scene scene;
    private CommonUtil commonUtil;

    public App() {
        this.commonUtil = new CommonUtil();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String token = SessionManager.loadSessionToken();  // Get the token (from the database or session)

        if (token != null) {
            try {
                long userId = JwtUtil.parseToken(token);  // Parse the token to get the userId

                // Optionally, use the userId to fetch more details (like username) from the database
                // For example:
                // User user = getUserFromDatabase(userId);
                // String username = user.getUsername();

                // If the token is valid and we have a user ID, proceed
                System.out.println("Welcome back, user ID: " + userId);

                scene = new Scene(loadFXML("Workspace"));  // Load the main workspace screen
                primaryStage.setScene(scene);
            } catch (Exception e) {
                // If token is invalid or can't be parsed, show the login screen
                System.out.println("Opps? ");
                scene = new Scene(loadFXML("Login"));
                primaryStage.setScene(scene);
            }
        } else {
            // No token means the user is not logged in, show the login screen
            scene = new Scene(loadFXML("Login"));
            primaryStage.setScene(scene);
        }

        primaryStage.show();
    }

    // Helper method to load FXML files
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Main entry point for the application
    public static void main(String[] args) {
        launch(args);
    }
}
