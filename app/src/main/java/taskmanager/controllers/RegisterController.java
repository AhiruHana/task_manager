package taskmanager.controllers;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import taskmanager.App;
import taskmanager.entities.User;
import taskmanager.entities.Workspace;
import taskmanager.utils.HibernateUtil;
import taskmanager.utils.PasswordUtil;

public class RegisterController {

    @FXML
    private Hyperlink Login;

    @FXML
    private Button SignIn;

    @FXML
    private BorderPane borderPane;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    void goToLogIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Login.getScene().getWindow();

            Scene scene = new Scene(root, 1024, 864);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void signUp(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String username = usernameField.getText();

        if (username.equals("")) {
            showErrorAlert("Oops", "Username must be not empty!");
        } else if (password.equals("")) {
            showErrorAlert("Oops", "Password must be not empty!");
        } else if (confirmPassword.equals("")) {
            showErrorAlert("Oops", "Confirm Password must be not empty!");
        } else if (email.equals("")) {
            showErrorAlert("Oops", "Email must be not empty!");
        } else if (!confirmPassword.equals(password)) {
            showErrorAlert("Oops", "Password and confirm password do not match!");
        } else {
            SessionFactory factory = HibernateUtil.getFactory();
            Session session = factory.openSession();
            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();

                // Kiểm tra username và email
                Long usernameCount = session
                        .createQuery("SELECT COUNT(*) FROM User u WHERE u.username = :username", Long.class)
                        .setParameter("username", username)
                        .getSingleResult();

                Long emailCount = session.createQuery("SELECT COUNT(*) FROM User u WHERE u.email = :email", Long.class)
                        .setParameter("email", email)
                        .getSingleResult();

                if (usernameCount > 0) {
                    throw new RuntimeException("Username is existed, please try again!");
                } else if (emailCount > 0) {
                    throw new RuntimeException("Email is existed, please try again!");
                }

                // Tạo user mới
                RegisterController registerController = new RegisterController();
                int result = registerController.registerUser(firstName, lastName, email, password, username, session);

                if (result > 0) {
                    // Lấy ID của user vừa được thêm
                    User newUser = (User) session.createQuery("FROM User u WHERE u.username = :username")
                            .setParameter("username", username)
                            .getSingleResult();

                    // Tạo workspace mới
                    Workspace workspace = new Workspace();
                    workspace.setName(username + "'s workspace");
                    workspace.setUser(newUser);

                    session.save(workspace); // Lưu workspace

                    // Commit toàn bộ giao dịch
                    transaction.commit();

                    // Hiển thị thông báo thành công
                    Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    successAlert.setContentText("Register Successfully");
                    successAlert.show();

                    // Chuyển sang giao diện Login
                    try {
                        double width = 1024;
                        double height = 864;

                        Parent root = FXMLLoader.load(App.class.getResource("/fxml/Login.fxml"));
                        Scene scene = new Scene(root, width, height);

                        Stage stage = (Stage) borderPane.getScene().getWindow();
                        stage.setScene(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("Registration failed!");
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback(); // Rollback nếu có lỗi
                }
                e.printStackTrace();
                showErrorAlert("Oops", "An error occurred: " + e.getMessage());
            } finally {
                session.close(); // Đóng session
            }
        }
    }

    public int registerUser(String firstName, String lastName, String email, String password, String username,
                        Session session) {
    try {
        String sql = "INSERT INTO users (username, first_Name, last_Name, email, password_digest) " +
                     "VALUES (:username, :firstName, :lastName, :email, :password_digest)";

        Query query = session.createSQLQuery(sql);
        query.setParameter("username", username);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        query.setParameter("email", email);
        query.setParameter("password_digest", PasswordUtil.hashPassword(password));

        return query.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
        return 0;
    }
}

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
