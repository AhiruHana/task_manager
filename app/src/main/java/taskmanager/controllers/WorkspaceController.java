package taskmanager.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WorkspaceController implements Initializable {
    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox menuSideBar;

    @FXML
    private Button menu1;
    @FXML
    private Button menu2;

    @FXML
    private Button menu3;

    @FXML
    private Button menu4;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borderPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            responseWidth(newWidth.doubleValue());

        });

    }

    public void responseWidth(double totalWidth) {
        double menuSideBarWidth = totalWidth * 0.2;
        menu1.setPrefWidth(menuSideBarWidth);
        menu2.setPrefWidth(menuSideBarWidth);
        menu3.setPrefWidth(menuSideBarWidth);
        menu4.setPrefWidth(menuSideBarWidth);
        menuSideBar.setPrefWidth(menuSideBarWidth);
    }
}
