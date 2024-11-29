package taskmanager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import taskmanager.services.AuthenticationService;
import taskmanager.utils.CommonUtil;
import taskmanager.exceptions.AuthenticationFailed;

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
            authService.authenticate(username, password);

            commonUtil.openMainApp(borderPane);
        } catch (AuthenticationFailed e) {
            commonUtil.showErrorMessage("Login failed!", "Invalid username or password");
        }
    }

    @FXML
    public void GoToSignUp(ActionEvent event) {
        commonUtil.newScene(borderPane);
    }
}
