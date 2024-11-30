package taskmanager.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import taskmanager.entities.User;
import taskmanager.entities.Workspace;
import taskmanager.services.SessionManager;
import taskmanager.utils.AuthenticationUtil;
import taskmanager.utils.CommonUtil;
import taskmanager.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;

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

    private AuthenticationUtil authUtil;
    private User currentUser;

    public void initialize() {
        try {
            URL cssUrl = getClass().getResource("/css/Workspace.css");
            if (cssUrl != null) {
                borderPane.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("CSS file not found!");
            }

            this.authUtil = new AuthenticationUtil();

            this.currentUser = authUtil.currentUser();
            displayProjectList();
            displayRecentOpened();
            getInfoUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            double width = 1024;
            double height = 864;

            Parent root = FXMLLoader.load(App.class.getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root, width, height);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);

            SessionManager.clearSessionToken();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomColor() {
        Random random = new Random();

        int r1 = random.nextInt(256);
        int g1 = random.nextInt(256);
        int b1 = random.nextInt(256);
        String color1 = String.format("#%02x%02x%02x", r1, g1, b1);

        int r2 = random.nextInt(256);
        int g2 = random.nextInt(256);
        int b2 = random.nextInt(256);
        String color2 = String.format("#%02x%02x%02x", r2, g2, b2);

        return color1 + ", " + color2;
    }

    public void responseWidth(double totalWidth) {
        double menuSideBarWidth = totalWidth * 0.2;
        info_username.setPrefWidth(menuSideBarWidth);
        info_fullname.setPrefWidth(menuSideBarWidth);
        info_email.setPrefWidth(menuSideBarWidth);
        logout.setPrefWidth(menuSideBarWidth);
        menuSideBar.setPrefWidth(menuSideBarWidth);
    }

    public void displayProjectList() {
        String username = currentUser.getUsername();
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createQuery(
                "SELECT b FROM Board b " +
                        "JOIN b.workspace w " +
                        "JOIN w.user u " +
                        "WHERE u.username = :username");
        query.setParameter("username", username);
        List<Board> boards = query.list();

        int colIndex = 1;
        int rowIndex = 0;

        for (Board d : boards) {
            Long id = d.getId();
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
                boardVbox(vBox, gradientText, name, boards, id,session);

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

        //create board
        vBox.setOnMouseClicked(event -> {
            try {
                session.beginTransaction();
                Query getWorkspacequery = session.createQuery(
                        "SELECT w FROM Workspace w " +
                                "JOIN w.user u " +
                                "WHERE u.username = :username");
                getWorkspacequery.setParameter("username", username);
                Workspace workspace = (Workspace) getWorkspacequery.getSingleResult();

                Board newBoard = new Board();
                newBoard.setName("Board A");
                newBoard.setColor(generateRandomColor());
                newBoard.setLastOpened(LocalDateTime.now());
                newBoard.setWorkspace(workspace);

                Long boardId = (Long) session.save(newBoard);
                session.getTransaction().commit();

                double width = borderPane.getScene().getWidth();
                double height = borderPane.getScene().getHeight();

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/Board.fxml"));
                Parent root = loader.load();

                BoardController boardController = loader.getController();
                boardController.displayBoardName(boardId);
                boardController.setId(boardId);

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

    public void displayRecentOpened() {
        String username = currentUser.getUsername();
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Query query = session.createQuery(
                "SELECT b FROM Board b " +
                        "JOIN b.workspace w " +
                        "JOIN w.user u " +
                        "WHERE u.username = :username ORDER BY last_opened DESC");
        query.setParameter("username", username);
        query.setMaxResults(3);

        List<Board> boards = query.list();

        int colIndex = 0;
        for (Board b : boards) {

            Long id = b.getId();
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
                boardVbox(vBox, gradientText, name, boards, id, session);
                GridPane.setConstraints(vBox, colIndex, 0);
                recentlyOpened.getChildren().add(vBox);

                colIndex++;
            }
        }
    }

    public void getInfoUser() {
        info_username.setText(currentUser.getUsername());
        info_fullname.setText("Full Name: " + currentUser.getFirstName() + " " + currentUser.getLastName());
        info_email.setText("Email : " + currentUser.getEmail());
    }

    public void boardVbox(VBox vBox, String gradientText, String name, List<Board> boards, Long id, Session session) {

        vBox.setPrefHeight(70.0);
        vBox.setPrefWidth(190.0);
        vBox.setStyle(gradientText);

        Label label = new Label(name);
        label.setFont(new Font("System Italic", 12.0));
        label.setStyle("-fx-text-fill: white;");
        VBox.setMargin(label, new Insets(10.0));
        getInfoUser();

        vBox.getChildren().add(label);

        vBox.setOnMouseClicked(event -> {
            session.beginTransaction();

            Board board = session.get(Board.class, id);
            board.setLastOpened(LocalDateTime.now());
            Long boardId = board.getId();

            session.update(board);
            session.getTransaction().commit();

            try {
                double width = borderPane.getScene().getWidth();
                double height = borderPane.getScene().getHeight();

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/Board.fxml"));
                Parent root = loader.load();

                BoardController boardController = loader.getController();
                boardController.displayBoardName(boardId);
                boardController.setId(boardId);

                Scene scene = new Scene(root, width, height);
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
