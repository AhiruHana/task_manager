package taskmanager.controllers;

import java.io.IOException;

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
import javafx.scene.control.Hyperlink;
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

    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
    
        if (username.equals("admin") && password.equals("123456")) {
            // Hiển thị thông báo đăng nhập thành công
            try {
                // Load trang mới
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Workspace.fxml"));
                Parent root = loader.load();
    
                // Lấy Stage hiện tại
                Stage stage = (Stage) Login.getScene().getWindow();
    
                // Tạo một Scene mới và đặt nó trên Stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Hiển thị thông báo đăng nhập thất bại
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password!");
            alert.showAndWait();
        }
    }

    @FXML
    void ClickSignUp(ActionEvent event) {
        try {
        // Load trang FXML mới
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml"));
        Parent root = loader.load();

        // Lấy Stage hiện tại
        Stage stage = (Stage) SignUp.getScene().getWindow();

        // Tạo một Scene mới và đặt nó trên Stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    }
