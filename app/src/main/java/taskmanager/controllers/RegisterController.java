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

import taskmanager.App;
import taskmanager.entities.User;
import taskmanager.entities.Workspace;
import taskmanager.utils.AuthenticationUtil;
import taskmanager.utils.CommonUtil;
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

    private CommonUtil commonUtil;
    private AuthenticationUtil authUtil;

    public void initialize() {
        try {
            this.commonUtil = new CommonUtil();
            this.authUtil = new AuthenticationUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToLogIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Login.getScene().getWindow();

            Scene scene = new Scene(root, 1024, 864);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signUp(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String username = usernameField.getText();

        if (username.equals("")) {
            commonUtil.showErrorAlert("Oops", "Username must be not empty!");
        } else if (password.equals("")) {
            commonUtil.showErrorAlert("Oops", "Password must be not empty!");
        } else if (confirmPassword.equals("")) {
            commonUtil.showErrorAlert("Oops", "Confirm Password must be not empty!");
        } else if (email.equals("")) {
            commonUtil.showErrorAlert("Oops", "Email must be not empty!");
        } else if (!confirmPassword.equals(password)) {
            commonUtil.showErrorAlert("Oops", "Password and confirm password do not match!");
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
                Long userId = registerController.registerUser(firstName, lastName, email, password, username);

                if (userId > 0) {
                    User user = User.findByEmail(email);
                    Workspace workspace = new Workspace();
                    workspace.setName(user.getUsername() + "'s workspace");
                    workspace.setUser(user);

                    session.save(workspace);

                    transaction.commit();

                    commonUtil.showSuccessMessage(Alert.AlertType.INFORMATION, "Register Successfully");
                    authUtil.signIn(user.getId());
                    commonUtil.openMainApp(borderPane);
                } else {
                    commonUtil.showErrorAlert("Registration Error", "Failed to register.");
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                commonUtil.showErrorAlert("Oops", "An error occurred: " + e.getMessage());
            } finally {
                session.close();
            }
        }
    }

    public Long registerUser(String firstName, String lastName, String email, String password, String username) {
    try {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPasswordDigest(PasswordUtil.hashPassword(password));

        Long userId = (Long) session.save(newUser);

        transaction.commit();

        return userId;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}
