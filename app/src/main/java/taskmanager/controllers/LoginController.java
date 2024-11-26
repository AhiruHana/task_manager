package taskmanager.controllers;

import java.io.IOException;
import java.net.URL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
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
import taskmanager.utils.HibernateUtil;

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
    void loginButtonClicked(ActionEvent event) {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();

        String username = usernameField.getText();
        String password = passwordField.getText();

        // TODO: Fix this query
        Query query = session.createQuery(
                "FROM User WHERE (email = :username or username= :username) AND password_digest = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<User> users = query.getResultList();

        session.close();

        if (!users.isEmpty()) {
            try {
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
