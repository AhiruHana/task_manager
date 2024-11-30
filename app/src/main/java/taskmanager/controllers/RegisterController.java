package taskmanager.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            commonUtil = new CommonUtil();
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
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String username = usernameField.getText().trim();

        if (!confirmPassword.equals(password)) {
            CommonUtil.showErrorAlert("Oops", "Password and confirm password do not match!");
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

                Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

                if (firstName == "") {
                    throw new RuntimeException("First Name can't be blank");
                } else if (lastName == "") {
                    throw new RuntimeException("Last Name can't be blank");
                } else if (username == "") {
                    throw new RuntimeException("Username can't be blank");
                } else if (email == "") {
                    throw new RuntimeException("Email can't be blank");
                }

                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

                if (!matcher.matches()) {
                    throw new RuntimeException("Email is in the wrong format");
                }

                if (usernameCount > 0) {
                    throw new RuntimeException("Username is existed, please try again!");
                } else if (emailCount > 0) {
                    throw new RuntimeException("Email is existed, please try again!");
                }


                // Tạo user mới
                RegisterController registerController = new RegisterController();
                User user = registerController.registerUser(firstName, lastName, email, password, username);

                if (user != null) {
                    Workspace workspace = new Workspace();
                    workspace.setName(user.getUsername() + "'s workspace");
                    workspace.setUser(user);

                    session.save(workspace);

                    transaction.commit();

                    CommonUtil.showSuccessMessage(Alert.AlertType.INFORMATION, "Register Successfully");
                    authUtil.signIn(user.getId());
                    CommonUtil.openMainApp(borderPane);
                } else {
                    CommonUtil.showErrorAlert("Registration Error", "Failed to register.");
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                CommonUtil.showErrorAlert("Oops", "An error occurred: " + e.getMessage());
            } finally {
                session.close();
            }
        }
    }

    public User registerUser(String firstName, String lastName, String email, String password, String username) {
    try {
        User newUser = new User(username, firstName, lastName, email, password);

        String errorMessage = User.saveUser(newUser);

        if (errorMessage != null) {
            commonUtil.showErrorMessage("Validation Error", errorMessage);
            return null;
        }

        return newUser;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}
