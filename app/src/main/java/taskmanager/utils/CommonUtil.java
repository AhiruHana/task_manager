package taskmanager.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import taskmanager.App;
import taskmanager.controllers.WorkspaceController;
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

  public void openMainApp(String username, BorderPane borderPane) {
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

  public void signIn(Long userId) {
    String token = JwtUtil.generateToken(userId);
    SessionManager.saveSessionToken(token);
  }
}
