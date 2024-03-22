package taskmanager.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import taskmanager.App;
import taskmanager.entities.Board;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class WorkspaceController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox menuSideBar;

    @FXML
    private Label info_username;
    @FXML
    private Label info_fullname;

    @FXML
    private Label info_email;

    @FXML
    private GridPane boardList;

    @FXML
    private GridPane recentlyOpened;

    @FXML
    private Button logout;

    @FXML
    private void logout(ActionEvent event) {
        try {
            double width = borderPane.getScene().getWidth();
            double height = borderPane.getScene().getHeight();

            Parent root = FXMLLoader.load(App.class.getResource("/Login.fxml"));
            Scene scene = new Scene(root, width, height);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    // @Override
    // public void initialize(URL location, ResourceBundle resources) {
    // borderPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
    // responseWidth(newWidth.doubleValue());

    // });

    // displayProjectList();
    // diplayRecentOpened();

    // }

    private String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public void responseWidth(double totalWidth) {
        double menuSideBarWidth = totalWidth * 0.2;
        info_username.setPrefWidth(menuSideBarWidth);
        info_fullname.setPrefWidth(menuSideBarWidth);
        info_email.setPrefWidth(menuSideBarWidth);
        logout.setPrefWidth(menuSideBarWidth);
        menuSideBar.setPrefWidth(menuSideBarWidth);
    }

    void displayProjectList() {

        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createQuery(
                "SELECT b FROM Board b " +
                        "JOIN b.workspace w " +
                        "JOIN w.user u " +
                        "WHERE u.username = :username or email= : username");
        query.setParameter("username", username);
        List<Board> boards = query.list();

        int colIndex = 1;
        int rowIndex = 0;
        System.out.println(username);
        for (Board d : boards) {
            int id = d.getId();
            String name = d.getName();
            String color = d.getColor();

            String queryString = query.getQueryString();
            System.out.println(queryString);
            System.out.println(username);
            String[] colorArray = color.split(",");

            if (colorArray.length >= 2) {
                String color1 = colorArray[0].trim();
                String color2 = colorArray[1].trim();

                String gradientText = String.format(
                        "-fx-background-color: linear-gradient(to right, %s, %s); -fx-background-radius: 5.0;",
                        color1, color2);

                VBox vBox = new VBox();
                boardVbox(vBox, gradientText, name, boards, id);
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

        vBox.setOnMouseClicked(event -> {
            try {
                double width = borderPane.getScene().getWidth();
                double height = borderPane.getScene().getHeight();

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/Board.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, width, height);
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    void diplayRecentOpened() {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createQuery(
                "SELECT b FROM Board b " +
                        "JOIN b.workspace w " +
                        "JOIN w.user u " +
                        "WHERE u.username = :username or email= : username ORDER BY lastOpened DESC");
        query.setParameter("username", username);
        query.setMaxResults(3);

        List<Board> boards = query.list();

        int colIndex = 0;
        for (Board b : boards) {

            int id = b.getId();
            String name = b.getName();
            String color = b.getColor();

            String[] colorArray = color.split(",");

            if (colorArray.length >= 2) {
                String color1 = colorArray[0].trim();
                String color2 = colorArray[1].trim();

                String gradientText = String.format(
                        "-fx-background-color: linear-gradient(to right, %s, %s); -fx-background-radius: 5.0;",
                        color1, color2);

                VBox vBox = new VBox();
                boardVbox(vBox, gradientText, name, boards, id);
                GridPane.setConstraints(vBox, colIndex, 0);
                recentlyOpened.getChildren().add(vBox);

                colIndex++;
            }
        }
    }

    public void getInfoUser() {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createQuery(
                "select username, firstName, lastName, email from User u WHERE u.username = :username or email= : username ");
        query.setParameter("username", username);

        List<Object[]> results = query.list();

        Object[] row = results.get(0);
        String fetchedUsername = (String) row[0];
        String fetchedFirstName = (String) row[1];
        String fetchedLastName = (String) row[2];
        String fetchedEmail = (String) row[3];

        info_username.setText(fetchedUsername);
        info_fullname.setText("Full Name: " + fetchedFirstName + " " + fetchedLastName);
        info_email.setText("Email : "+fetchedEmail);

    }

    public void boardVbox(VBox vBox, String gradientText, String name, List<Board> boards, int id) {

        vBox.setPrefHeight(70.0);
        vBox.setPrefWidth(190.0);
        vBox.setStyle(gradientText);

        Label label = new Label(name);
        label.setFont(new Font("System Italic", 12.0));
        label.setStyle("-fx-text-fill: white;");
        VBox.setMargin(label, new Insets(10.0));

        vBox.getChildren().add(label);

        vBox.setOnMouseClicked(event -> {
            SessionFactory factory = HibernateUtil.getFactory();
            Session session = factory.openSession();
            Transaction transaction = session.beginTransaction();

            Board board = session.get(Board.class, id);
            board.setLastOpened(LocalDateTime.now());

            session.update(board);
            transaction.commit();
            session.close();

            try {
                double width = borderPane.getScene().getWidth();
                double height = borderPane.getScene().getHeight();

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/Board.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, width, height);
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
