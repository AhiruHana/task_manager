package taskmanager.controllers;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.List;

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
import taskmanager.entities.Board;
import taskmanager.entities.User;
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
    SessionFactory factory = HibernateUtil.getFactory();
    Session session = factory.openSession();

    String username = usernameField.getText();
    String password = passwordField.getText();

    Query query = session.createQuery("FROM User WHERE first_name = :firstname AND password_hash = :password");
    query.setParameter("firstname", username);
    query.setParameter("password", password);
    List<User> users = query.getResultList();

    session.close();

    if (!users.isEmpty()) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Workspace.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Login.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
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
