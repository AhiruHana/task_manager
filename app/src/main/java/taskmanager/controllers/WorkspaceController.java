package taskmanager.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import taskmanager.entities.Board;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

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

    @FXML
    private GridPane boardList;

    @FXML
    private GridPane recentlyOpened;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borderPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            responseWidth(newWidth.doubleValue());

        });

        // var data = createData();

        // displayProjectList(data);
        // diplayRecentOpened(data);

        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createSQLQuery("SELECT * FROM boards").addEntity(Board.class);
        // List<Object[]> result = query.list();   

        // List<Board> boards = new ArrayList<>();
        // for (Object[] row : result) {
        //     Board board = new Board();
        //     board.setId((int) row[0]);
        //     board.setName((String) row[1]);
        //     board.setLastOpened((LocalDateTime)row[2]);
        //     board.setColor((String) row[3]);
        //     boards.add(board);
        // }
        List<Board> boards = query.list();
displayProjectList(boards);

        // displayProjectList(boards);

        session.close();
    }

    private String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public void responseWidth(double totalWidth) {
        double menuSideBarWidth = totalWidth * 0.2;
        menu1.setPrefWidth(menuSideBarWidth);
        menu2.setPrefWidth(menuSideBarWidth);
        menu3.setPrefWidth(menuSideBarWidth);
        menu4.setPrefWidth(menuSideBarWidth);
        menuSideBar.setPrefWidth(menuSideBarWidth);
    }

    private static Map<String, Object> createEntry(int id, String name, String lastOpened, String color1,
            String color2) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", id);
        entry.put("name", name);
        entry.put("lastOpened", lastOpened);
        entry.put("color1", color1);
        entry.put("color2", color2);
        return entry;
    }

    private List<Map<String, Object>> createData() {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> entry1 = createEntry(1, "Janetta", "24-08-2023 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry1);

        Map<String, Object> entry2 = createEntry(2, "Jobyna", "19-08-2023 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry2);

        Map<String, Object> entry3 = createEntry(3, "Earle", "23-12-2023 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry3);

        Map<String, Object> entry4 = createEntry(4, "Granger", "13-12-2023 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry4);

        Map<String, Object> entry5 = createEntry(5, "Westbrook", "13-08-2024 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry5);

        Map<String, Object> entry6 = createEntry(6, "Emlynn", "13-09-2023 00:00:00", generateRandomColor(),
                generateRandomColor());
        data.add(entry6);
        return data;
    }

    private void displayProjectList(List<Board> data) {
        int colIndex = 1;
        int rowIndex = 0;
        for (Board d : data) {
            int id = d.getId();
            String name = d.getName();
            String color = d.getColor();

            String[] colorArray = color.split(",");

            if (colorArray.length >= 2) {
                String color1 = colorArray[0].trim();
                String color2 = colorArray[1].trim();

                String gradientText = String.format(
                        "-fx-background-color: linear-gradient(to right, %s, %s); -fx-background-radius: 5.0;",
                        color1, color2);

                VBox vBox = new VBox();
                boardVbox(vBox, gradientText, name, data);
                GridPane.setConstraints(vBox, colIndex, rowIndex);
                boardList.getChildren().add(vBox);

                colIndex++;
                if (colIndex == 4) {
                    colIndex = 0;
                    rowIndex++;
                }
            }
        }

        VBox vBox = new VBox();
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        vBox.setPrefHeight(70.0);
        vBox.setPrefWidth(190.0);
        vBox.setStyle("-fx-background-color: #F1F2F4; -fx-background-radius: 5.0;");

        Label label = new Label("Create New Board");
        label.setFont(new Font("System Italic", 12.0));
        VBox.setMargin(label, new Insets(10.0));

        vBox.getChildren().add(label);
        GridPane.setConstraints(vBox, 0, 0);
        boardList.getChildren().add(vBox);

        for (int i = 0; i <= rowIndex; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(95.0);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            boardList.getRowConstraints().add(rowConstraints);
        }
    }

    private void diplayRecentOpened(List<Map<String, Object>> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        data.sort(Comparator.comparing((Map<String, Object> entry) -> {
            String lastOpened = (String) entry.get("lastOpened");
            LocalDate lastOpenedDate = LocalDate.parse(lastOpened, formatter);
            return lastOpenedDate;
        }).reversed());

        List<Map<String, Object>> recentEntries = data.stream()
                .limit(3)
                .collect(Collectors.toList());

        int colIndex = 0;
        for (Map<String, Object> entry : recentEntries) {
            int id = (int) entry.get("id");
            String name = (String) entry.get("name");
            String color1 = (String) entry.get("color1");
            String color2 = (String) entry.get("color2");
            String gradientText = String.format(
                    "-fx-background-color: linear-gradient(to right, %s, %s); -fx-background-radius: 5.0;",
                    color1.toString(), color2.toString());

            VBox vBox = new VBox();
            // boardVbox(vBox, gradientText, name, entry);

            GridPane.setConstraints(vBox, colIndex, 0);
            recentlyOpened.getChildren().add(vBox);

            colIndex++;
        }

    }

    private void boardVbox(VBox vBox, String gradientText, String name, List<Board> boards) {

        vBox.setPrefHeight(70.0);
        vBox.setPrefWidth(190.0);
        vBox.setStyle(gradientText);

        Label label = new Label(name);
        label.setFont(new Font("System Italic", 12.0));
        label.setStyle("-fx-text-fill: white;");
        VBox.setMargin(label, new Insets(10.0));

        vBox.getChildren().add(label);

        vBox.setOnMouseClicked(event -> {

            Stage previousStage = (Stage) vBox.getScene().getWindow();
            Scene previousScene = previousStage.getScene();

            VBox newVBox = new VBox();
            newVBox.getChildren().add(new Label("New Screen"));

            Button backButton = new Button("Back");
            backButton.setOnAction(backEvent -> {
                Stage currentStage = (Stage) backButton.getScene().getWindow();
                currentStage.close();
            });

            newVBox.getChildren().add(backButton);
            backButton.setOnAction(backEvent -> {
                SessionFactory factory = HibernateUtil.getFactory();
                Session session = factory.openSession();
                Transaction transaction = session.beginTransaction();

                Board board = session.get(Board.class, vBox.getId());
                board.setLastOpened(LocalDateTime.now());

                session.update(board);
                transaction.commit();

                previousStage.setScene(previousScene);
                previousStage.show();

            });

            Scene newScene = new Scene(newVBox);

            Stage currentStage = (Stage) vBox.getScene().getWindow();
            currentStage.setMaximized(true);
            currentStage.setScene(newScene);

            currentStage.show();

        });
    }
}
