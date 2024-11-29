package taskmanager.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import taskmanager.App;
import taskmanager.controllers.WorkspaceController;
import taskmanager.entities.User;
import taskmanager.services.SessionManager;

public class CommonUtil {
  public void showErrorMessage(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public void newScene(BorderPane borderPane) {
    try {
      double width = borderPane.getScene().getWidth();
      double height = borderPane.getScene().getHeight();

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
      Parent root = loader.load();

      Scene scene = new Scene(root, width, height);

      Stage stage = (Stage) borderPane.getScene().getWindow();
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void openMainApp(BorderPane borderPane) {
    try {
        double width = borderPane.getScene().getWidth();
        double height = borderPane.getScene().getHeight();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/Workspace.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setScene(scene);
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public void signIn(Long userId) {
    String token = JwtUtil.generateToken(userId);
    SessionManager.saveSessionToken(token);
  }

  public void showErrorAlert(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public void showSuccessMessage(Alert.AlertType type, String message) {
    Alert successAlert = new Alert(type);
    successAlert.setContentText("Register Successfully");
    successAlert.show();
  }

  public User currentUser() {
    String token = SessionManager.loadSessionToken();

    Long userId = JwtUtil.parseToken(token);

    return User.findById(userId);
  }
}
