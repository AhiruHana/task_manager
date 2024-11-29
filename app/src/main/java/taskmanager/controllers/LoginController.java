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
import taskmanager.entities.Workspace;
import taskmanager.services.AuthenticationService;
import taskmanager.services.SessionManager;
import taskmanager.utils.JwtUtil;
import taskmanager.utils.CommonUtil;
import taskmanager.utils.HibernateUtil;
import taskmanager.exceptions.AuthenticationFailed;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
    private CommonUtil commonUtil;

    public LoginController() {
        this.authService = new AuthenticationService(); // Initialize the service
        this.commonUtil = new CommonUtil();
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
            User user = authService.authenticate(username, password);

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);

            Long userId = (Long) claims.get("userId");
            String token = JwtUtil.generateToken(userId);
            SessionManager.saveSessionToken(token);

            openMainApp(username);
        } catch (AuthenticationFailed e) {
            commonUtil.showErrorMessage("Login failed!", "Invalid username or password");
        }
    }

    @FXML
    public void GoToSignUp(ActionEvent event) {
        commonUtil.newScene(borderPane);
    }

    private void openMainApp(String username) {

        try {

            double width = borderPane.getScene().getWidth();
            double height = borderPane.getScene().getHeight();

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/Workspace.fxml"));
            Parent root = loader.load();

            WorkspaceController workspaceController = loader.getController();
            workspaceController.displayProjectList(username);
            workspaceController.diplayRecentOpened(username);
            workspaceController.getInfoUser(username);

            Scene scene = new Scene(root, width, height);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
