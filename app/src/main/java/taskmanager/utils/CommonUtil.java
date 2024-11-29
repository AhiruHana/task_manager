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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
      Parent root = loader.load();

      Scene scene = new Scene(root, 1024, 864);

      Stage stage = (Stage) borderPane.getScene().getWindow();
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void openMainApp(BorderPane borderPane) {
    try {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/Workspace.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1024, 864);
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setScene(scene);
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public static void showErrorAlert(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void showSuccessMessage(Alert.AlertType type, String message) {
    Alert successAlert = new Alert(type);
    successAlert.setContentText(message);
    successAlert.show();
  }
}
