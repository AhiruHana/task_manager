package taskmanager;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import taskmanager.entities.User;
import taskmanager.services.SessionManager;
import taskmanager.utils.CommonUtil;
import taskmanager.utils.JwtUtil;

public class App extends Application {

    private static Scene scene;
    private double width = 1024;
    private double height = 864;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String token = SessionManager.loadSessionToken();

        if (token != null) {
            try {
                long userId = JwtUtil.parseToken(token);

                User currentUser = User.findById(userId);

                CommonUtil.showSuccessMessage(AlertType.INFORMATION, "Welcome back " + currentUser.getUsername());

                scene = new Scene(loadFXML("Workspace"), width, height);  // Load the main workspace screen
                primaryStage.setMaximized(true);
                primaryStage.setScene(scene);
            } catch (Exception e) {
                scene = new Scene(loadFXML("Login"), width, height);
                primaryStage.setMaximized(true);
                primaryStage.setScene(scene);
            }
        } else {
            // No token means the user is not logged in, show the login screen
            scene = new Scene(loadFXML("Login"), width, height);
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
        }
        primaryStage.show();
    }

    // Helper method to load FXML files
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/"+fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Main entry point for the application
    public static void main(String[] args) {
        launch(args);
    }
}
