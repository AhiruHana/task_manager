package taskmanager.controllers;

import java.io.IOException;

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
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import taskmanager.entities.Board;
import taskmanager.entities.User;


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
    void ClickLogin(ActionEvent event) {
        try {
        // Load trang FXML mới
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
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
    }


    @FXML
    void clickSignUp(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            showErrorAlert("Password mismatch", "The passwords do not match.");
            return;
        }
        RegisterController registerController = new RegisterController();
        registerController.registerUser(firstName, lastName, email, password);
        // Gọi phương thức registerUser() từ controller RegisterController để xử lý đăng ký người dùng

    }


    private void registerUser(String firstName, String lastName, String email, String password) {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Tạo câu lệnh INSERT INTO với tham số hóa
            String sql = "INSERT INTO users (first_Name, last_Name, email, password_hash) VALUES (:firstName, :lastName, :email, :password)";

            // Tạo truy vấn từ câu lệnh SQL
            Query query = session.createSQLQuery(sql);

            // Đặt giá trị cho các tham số
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            query.setParameter("email", email);
            query.setParameter("password", password);

            // Thực thi truy vấn
            int result = query.executeUpdate();

            // Commit giao dịch
            transaction.commit();
        } catch (Exception e) {
            // Xảy ra lỗi, rollback giao dịch
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            // Đóng session
            session.close();
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
