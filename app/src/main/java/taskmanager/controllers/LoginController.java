package taskmanager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import taskmanager.App;
import taskmanager.entities.User;
import taskmanager.services.AuthenticationService;
import taskmanager.exceptions.AuthenticationFailed;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Hyperlink SignUp;

    @FXML
    private Button Login;

    private AuthenticationService authService;

    public LoginController() {
        this.authService = new AuthenticationService(); // Initialize the service
    }

    public void initialize() {
        try {
            URL cssUrl = getClass().getResource("/css/Login.css");
            if (cssUrl != null) {
                borderPane.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("CSS file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void authenticate(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // TODO: Use this user to get workspaces
            User user = authService.authenticate(username, password); // Delegate to the service

            double width = borderPane.getScene().getWidth();
            double height = borderPane.getScene().getHeight();

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Workspace.fxml"));
            Parent root = loader.load();

            WorkspaceController workspaceController = loader.getController();
            workspaceController.setUsername(username);
            workspaceController.displayProjectList();
            workspaceController.diplayRecentOpened();
            workspaceController.getInfoUser();

            Scene scene = new Scene(root, width, height);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
        } catch (AuthenticationFailed e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password!");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ClickSignUp(ActionEvent event) {
        try {
            double width = borderPane.getScene().getWidth();
            double height = borderPane.getScene().getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
