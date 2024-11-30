package taskmanager.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import taskmanager.entities.Board;
import taskmanager.entities.Col;
import taskmanager.entities.Task;
import taskmanager.entities.Workspace;
import taskmanager.utils.CommonUtil;
import taskmanager.utils.HibernateUtil;

public class BoardController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox main;

    @FXML
    private VBox addListVbox;

    @FXML
    private Button addColButton;

    @FXML
    private VBox addColTextField;

    @FXML
    private TextField addListTextField;

    @FXML
    private Label boardNameLabel;

    @FXML
    private TextField boardNameTextfield;

    private VBox draggedVBox = null;
    private Long draggedTaskId;
    private Long targetColId;

    private Long boardId;

    public void setId(Long id) {
        this.boardId = id;
    }

    private Col col;

    public void setCol(Col col) {
        this.col = col;
    }

    public void displayBoardName(Long boardId) {

        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();

        try {

            Board board = session.get(Board.class, boardId);

            if (board != null) {
                String color = board.getColor();

                String[] colorArray = color.split(",");

                if (colorArray.length >= 2) {
                    String color1 = colorArray[0].trim();
                    String color2 = colorArray[1].trim();

                    String gradientText = String.format(
                            "-fx-background: linear-gradient(to right, %s, %s)",
                            color1, color2);

                    scrollPane.setStyle(gradientText);
                }

                boardNameLabel.setText(board.getName());
                boardNameLabel.setOnMouseClicked(event -> {

                    boardNameLabel.setVisible(false);
                    boardNameLabel.setManaged(false);
                    boardNameTextfield.setVisible(true);

                    boardNameTextfield.setText(board.getName());

                    boardNameTextfield.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (isNowFocused) {
                            boardNameTextfield.selectAll();
                        }
                    });

                    boardNameTextfield.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            String newName = boardNameTextfield.getText();

                            boardNameLabel.setText(newName);

                            boardNameTextfield.setVisible(false);
                            boardNameTextfield.setManaged(false);
                            boardNameLabel.setVisible(true);

                            updateBoardName(boardId, newName);
                        }
                    });
                });

            } else {
                System.out.println("No board found with ID: " + boardId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    @FXML
    void goToWorkspace(ActionEvent event) {
        CommonUtil.openMainApp(borderPane);
    }

    @FXML
    private void addCol(ActionEvent event) {
        addColButton.setVisible(false);
        addColButton.setManaged(false);
        addColTextField.setVisible(true);
    }

    @FXML
    private void closeAddColAction(ActionEvent event) {
        addColButton.setVisible(true);
        addColTextField.setManaged(false);
        addColTextField.setVisible(false);
    }

    @FXML
    private void addListAction(ActionEvent event) {
        String colName = addListTextField.getText();

        if (colName != null && !colName.isEmpty()) {

            SessionFactory factory = HibernateUtil.getFactory();
            Session session = factory.openSession();

            try {

                session.beginTransaction();
                Query getBoardquery = session.createQuery(
                        "SELECT b FROM Board b WHERE b.id = :boardId");

                getBoardquery.setParameter("boardId", boardId);
                Board board = (Board) getBoardquery.getSingleResult();

                Col newCol = new Col();
                newCol.setName(colName);
                newCol.setBoard(board);
                session.save(newCol);

                // setCol(newCol);
                session.getTransaction().commit();

                VBox colVBox = new VBox();
                colVBox.setStyle("-fx-border-color: none; -fx-background-color: #F1F2F4;");
                colVBox.setSpacing(10.0);
                colVBox.setMinWidth(270);

                colVBox.setUserData(newCol.getId());

                setDragAndDropEvents(colVBox);

                VBox colNameVBox = new VBox();

                // Tao Label colname
                Label colLabel = new Label(colName);
                colLabel.setMaxWidth(Double.MAX_VALUE);
                colLabel.setPadding(new Insets(6.0, 8.0, 6.0, 17.0));
                colLabel.setFont(Font.font("System", FontWeight.BOLD, 12.0));

                colNameVBox.getChildren().add(colLabel);

                // Tao Button Add a card
                VBox addCardButtonVBox = new VBox();
                Button addCardButton = new Button("Add a card");
                addCardButton.setMaxWidth(Double.MAX_VALUE);
                addCardButton.setMaxHeight(Double.MAX_VALUE);
                addCardButton.setStyle("-fx-background-color: #F1F2F4; -fx-background-radius: 10px;");
                addCardButton.setFont(Font.font("System", FontWeight.BOLD, 12.0));
                addCardButton.setAlignment(Pos.TOP_LEFT);

                Label icon = new Label("\uf067"); // unicode dau +
                icon.setStyle("-fx-font-family: 'FontAwesome'; -fx-font-size: 14;");
                addCardButton.setGraphic(icon);

                addCardButtonVBox.getChildren().add(addCardButton);
                addCardButton.setPadding(new Insets(5.0, 8.0, 5.0, 8.0));
                // VBox.setMargin(addCardButtonVBox, new Insets(0, 0, 60, 0));

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToWidth(true);
                scrollPane.setLayoutX(10.0);
                scrollPane.setLayoutY(10.0);
                scrollPane.setMinWidth(270.0);
                scrollPane.setVisible(false);
                scrollPane.setManaged(false);
                scrollPane.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");

                VBox mainVBox = new VBox();
                mainVBox.setStyle(" -fx-background-color: #F1F2F4;");
                mainVBox.setSpacing(10.0);
                scrollPane.setContent(mainVBox);

                // Tao TextField de nhap ten card
                TextField titleTextField = new TextField();
                titleTextField.setPromptText("Enter a title");
                titleTextField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
                titleTextField.setPadding(new Insets(7.0, 8.0, 7.0, 8.0));

                titleTextField.setVisible(false);
                titleTextField.setManaged(false);

                // Tao HBox chua button Add List va Close
                Button addListButton = new Button("Add card");
                addListButton.setStyle("-fx-background-color: #0055CC;");
                addListButton.setTextFill(Color.WHITE);
                addListButton.setFont(Font.font("System", FontWeight.BOLD, 12.0));

                addListButton.setUserData(newCol.getId());
                Button closeButton = new Button();
                closeButton.setStyle("-fx-background-color: #F1F2F4;");
                Label closeLabel = new Label("\u2715"); // Unicode dau "✕"
                closeLabel.setFont(Font.font(17.0));
                closeLabel.setTextFill(Color.BLACK);
                closeButton.setGraphic(closeLabel);

                HBox buttonBox = new HBox(addListButton, closeButton);
                buttonBox.setSpacing(10.0);
                buttonBox.setVisible(false);
                buttonBox.setManaged(false);

                // gan cac thanh phan vao vbox
                VBox inputVBox = new VBox(titleTextField, buttonBox);

                inputVBox.setSpacing(10.0);
                VBox.setMargin(inputVBox, new Insets(0, 10.0, 0, 10.0));

                addCardButton.setOnAction(e -> {
                    titleTextField.setVisible(true);
                    titleTextField.setManaged(true);
                    addCardButton.setVisible(false);
                    addCardButton.setManaged(false);
                    buttonBox.setVisible(true);
                    buttonBox.setManaged(true);

                    VBox.setMargin(addCardButtonVBox, new Insets(0, 0, 0, 0));

                });

                closeButton.setOnAction(e -> {
                    addCardButton.setVisible(true);
                    titleTextField.setManaged(false);
                    titleTextField.setVisible(false);
                    buttonBox.setVisible(false);
                    buttonBox.setManaged(false);

                });

                addListButton.setOnAction(e -> {
                    String title = titleTextField.getText();

                    if (title != null && !title.isEmpty()) {
                        try {

                            session.beginTransaction();

                            Task newTask = new Task();
                            newTask.setName(title);
                            Long attachColId = (Long) addListButton.getUserData();
                            col = Col.findById(attachColId);
                            newTask.setCol(col);
                            // newTask.setDescription("");
                            session.save(newTask);

                            session.getTransaction().commit();

                            // Tao Label cho card
                            Label cardLabel = new Label(title);
                            cardLabel.setMaxWidth(Double.MAX_VALUE);
                            cardLabel.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
                            cardLabel.setPadding(new Insets(7.0, 8.0, 7.0, 8.0));
                            VBox cardVBox = new VBox(cardLabel);
                            VBox.setMargin(cardVBox, new Insets(5.0, 8.0, 0, 8.0));
                            cardVBox.setUserData(newTask.getId());

                            cardVBox.setOnDragDetected(dragEvent -> {
                                draggedVBox = cardVBox;
                                draggedTaskId = (Long) cardVBox.getUserData();
                                Dragboard dragboard = cardVBox.startDragAndDrop(TransferMode.MOVE);
                                ClipboardContent content = new ClipboardContent();
                                content.putString(cardLabel.getText()); // Put the data of the card (e.g., ID or title)
                                dragboard.setContent(content);
                                dragEvent.consume();
                            });

                            cardVBox.setOnDragDone(dragEvent -> {
                                draggedVBox = null;

                                Task task = Task.findById(draggedTaskId);
                                Long colId = (Long) cardVBox.getParent().getUserData();
                                Col col = Col.findById(colId);
                                task.setCol(col);
                                Task.save(task);

                                draggedTaskId = null;
                                targetColId = null;
                                dragEvent.consume();
                            });

                            mainVBox.getChildren().addAll(cardVBox);
                            if (!mainVBox.getChildren().isEmpty()) {
                                scrollPane.setVisible(true);
                                scrollPane.setManaged(true);
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }

                    }

                    buttonBox.setVisible(false);
                    buttonBox.setManaged(false);
                    addCardButton.setVisible(true);
                    titleTextField.clear();
                    titleTextField.setVisible(false);
                    titleTextField.setManaged(false);

                    VBox.setMargin(addCardButtonVBox, new Insets(0, 0, 30, 0));
                });

                colVBox.getChildren().addAll(colNameVBox, scrollPane, addCardButtonVBox, inputVBox);

                int addListIndex = main.getChildren().indexOf(addListVbox);

                main.getChildren().add(addListIndex, colVBox);
                main.setMargin(colVBox, new Insets(0, 0, 0, 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        addListTextField.clear();
        addColTextField.setVisible(false);
        addColTextField.setManaged(false);
        addColButton.setVisible(true);

    }

    public void createCol(Long boardId, String name) {
        try {
            SessionFactory factory = HibernateUtil.getFactory();
            Session session = factory.openSession();

            session.beginTransaction();
            Query getBoardquery = session.createQuery(
                    "SELECT b FROM Board b WHERE b.id = :boardId");

            getBoardquery.setParameter("boardId", boardId);
            Board board = (Board) getBoardquery.getSingleResult();

            Col newCol = new Col();
            newCol.setName(name);
            newCol.setBoard(board);
            session.save(newCol);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDragAndDropEvents(VBox column) {
        column.setStyle("-fx-background-color: white; -fx-padding: 10;");
        column.setOnDragOver((DragEvent dragEvent) -> {
            if (dragEvent.getGestureSource() != column && dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
            dragEvent.consume();
        });

        column.setOnDragDropped(event -> {
            if (draggedVBox != null) {
                // Remove the VBox from its current parent and add to this column
                Pane currentParent = (Pane) draggedVBox.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(draggedVBox);

                    // currentParent.requestLayout();
                    currentParent.applyCss();
                    currentParent.layout();
                }

                int buttonIndex = column.getChildren().size() - 2;

                column.getChildren().add(buttonIndex, draggedVBox);

                column.applyCss();
                column.layout();
            }

            event.setDropCompleted(true); // Indicate the drop is completed
            event.consume();
        });
    }

    private void updateBoardName(Long boardId, String newName) {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Board board = session.get(Board.class, boardId);
            if (board != null) {
                board.setName(newName);
                session.update(board);
            }

            transaction.commit();
            System.out.println("Board name updated successfully!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
