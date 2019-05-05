package Logic;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;

import Body.Direction;
import Body.Grid;
import Body.Point;
import Logic.SnakeLogic;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static Body.Direction.*;

public class Main extends Application {

    private int TASK_UPDATE_PERIOD_MS = 150;
    private int TASK_UPDATE_DELAY_MS = TASK_UPDATE_PERIOD_MS;

    private static int WINDOW_HEIGHT = 600;
    private static int WINDOW_WIDTH = 600;
    private static int GRID_BLOCK_SIZE = 20;

    private GraphicsContext graphicsContext;
    private Button startButton;
    private Button exitButton;

    private Button SaveButton;
    private Button LoadButton;
    private Button settingsButton;
    private Button applyButton;
    private SnakeLogic snake;
    private Grid grid;
    private AnimationTimer animationTimer;
    private Timer timer;
    private TimerTask task;

    private Label lableSpeed;
    private Label colorSnake;
    private Label colorFood;
    private Label labelBlock;

    private TextField textSpeed;
    private TextField blocksSize;
    private Canvas canvas;
    private Group root;
    private Scene scene;
    private boolean isGameInProgress = false;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private static Color mycolorSnake = Color.GREEN;
    private static Color mycolorFood = Color.BLUE;

    @Override //  переопределяет объявление метода в базовом классе.
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Snake");// установка заголовка
        Group root = new Group(); // контейнер для группы объектов
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT); // полотно для отрисовки различного содержимого
        graphicsContext = canvas.getGraphicsContext2D();//  получаем GraphicContext
        root.getChildren().add(canvas); // добавляем объект Canvas в корневой контейнер
        Scene scene = new Scene(root);// создаем сцену для всех графических элементов
        primaryStage.setResizable(false); // запрет на расширения окна
        primaryStage.initStyle(StageStyle.DECORATED);// изменение формата окна
        /*Рисуем сетку размером WINDOW_WIDTH*WINDOW_HEIGHT, размер ячеек = GRID_BLOCK_SIZE*/
        grid = new Grid(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);

        /*Создаем экземпляр класса Snake*/
        snake = new SnakeLogic(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);

        /*Устанавливаем начальное положение змеи*/
        snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);

        /*Рисуем сетку */
        drawGrid();

        /*Рисуем кнопки */
        startButton = new Button("Start!");
        startButton.setMinWidth(100);
        startButton.setMinHeight(36);

        SaveButton = new Button("Save");
        SaveButton.setMinWidth(100);
        SaveButton.setMinHeight(36);

        LoadButton = new Button("Load");
        LoadButton.setMinWidth(100);
        LoadButton.setMinHeight(36);

        settingsButton = new Button("Settings");
        settingsButton.setMinWidth(100);
        settingsButton.setMinHeight(36);

        exitButton = new Button("Exit");
        exitButton.setMinWidth(100);
        exitButton.setMinHeight(36);

        /*Размещаем кнопку по центру */
        VBox vBox = new VBox();
        vBox.prefWidthProperty().bind(canvas.widthProperty());
        vBox.prefHeightProperty().bind(canvas.heightProperty());
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(startButton);
        vBox.setSpacing(10);
        vBox.getChildren().add(LoadButton);
        vBox.setSpacing(10);
        vBox.getChildren().add(SaveButton);
        vBox.setSpacing(10);
        vBox.getChildren().add(settingsButton);
        vBox.setSpacing(10);
        vBox.getChildren().add(exitButton);
        root.getChildren().add(vBox);

        /*Нажатие кнопки "Start!"*/
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                isGameInProgress = true;
                isGameOver = false;
                startButton.setVisible(false);
                exitButton.setVisible(false);
                settingsButton.setVisible(false);
                SaveButton.setVisible(false);
                LoadButton.setVisible(false);
                if (timer == null) {
                    task = createTimerTask();
                    timer = new Timer("Timer");
                    timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
                    animationTimer.start();
                }
            }
        });

        LoadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                isGameInProgress = true;
                isGameOver = false;
                startButton.setVisible(false);
                exitButton.setVisible(false);
                settingsButton.setVisible(false);
                SaveButton.setVisible(false);
                LoadButton.setVisible(false);
                int x = 0, y = 0, speed = 0, length = 0;
                String colorFood, colorSnake;
                File f = new File("src/save.txt");
                if (f.isFile()) {
                    try {
                        FileReader reader = new FileReader(f);
                        int c;
                        StringBuffer str = new StringBuffer();
                        while ((c = reader.read()) != -1) {
                            str.append((char) c);
                        }
                        int i = str.indexOf(" ");
                        String s = str.substring(0, i);
                        speed = Integer.parseInt(s);
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        s = str.substring(0, i);
                        x = Integer.parseInt(s);
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        s = str.substring(0, i);
                        y = Integer.parseInt(s);
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        s = str.substring(0, i);
                        length = Integer.parseInt(s);
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        colorSnake = str.substring(0, i);
                        switch (colorSnake) {
                            case "0xff0000ff":
                                mycolorSnake = Color.RED;
                                break;
                            case "0x008000ff":
                                mycolorSnake = Color.GREEN;
                                break;
                            case "0x0000ffff":
                                mycolorSnake = Color.BLUE;
                                break;
                            case "0xffc0cbff":
                                mycolorSnake = Color.PINK;
                                break;
                            case "0xffff00ff":
                                mycolorSnake = Color.YELLOW;
                                break;
                        }
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        colorFood = str.substring(0, i);
                        switch (colorFood) {
                            case "0xff0000ff":
                                mycolorFood = Color.RED;
                                break;
                            case "0x008000ff":
                                mycolorFood = Color.GREEN;
                                break;
                            case "0x0000ffff":
                                mycolorFood = Color.BLUE;
                                break;
                            case "0xffc0cbff":
                                mycolorFood = Color.PINK;
                                break;
                            case "0xffff00ff":
                                mycolorFood = Color.YELLOW;
                                break;
                        }
                        str.delete(0, i + 1);
                        i = str.indexOf(" ");
                        s = str.substring(0, i);
                        GRID_BLOCK_SIZE = Integer.parseInt(s);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                grid = new Grid(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
                snake = new SnakeLogic(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
                TASK_UPDATE_PERIOD_MS = speed;
                snake.setDirection(RIGHT);
                snake.setHeadLocation(x, y);
                //как реализовать длину???
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        snake.getTail().add(i, new Point(x - (i + 1) * GRID_BLOCK_SIZE, y));
                    }
                }
                if (timer == null) {
                    task = createTimerTask();
                    timer = new Timer("Timer");
                    timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
                    animationTimer.start();
                }

            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                StackPane settingsSack = new StackPane();

                Stage settingsStage = new Stage();
                settingsStage.setTitle("Settings");
                settingsStage.setX(primaryStage.getX() + 200);
                settingsStage.setY(primaryStage.getY() + 200);
                settingsStage.setResizable(false);


                ObservableList<String> colors = FXCollections.observableArrayList(
                        "Red",
                        "Green",
                        "Blue",
                        "Pink",
                        "Yellow"
                );
                ObservableList<Integer> speed = FXCollections.observableArrayList(
                        50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300
                );
                ObservableList<Integer> sizeBlock = FXCollections.observableArrayList(
                        10, 20, 30, 40
                );

                ComboBox comboBoxsizeBlock = new ComboBox(sizeBlock);
                comboBoxsizeBlock.getSelectionModel().selectFirst();
                ComboBox comboBoxSpeed = new ComboBox(speed);
                comboBoxSpeed.getSelectionModel().selectFirst();
                ComboBox comboBoxColorsSnake = new ComboBox(colors);
                comboBoxColorsSnake.getSelectionModel().selectFirst();
                ComboBox comboBoxColorsFood = new ComboBox(colors);
                comboBoxColorsFood.getSelectionModel().selectFirst();
                textSpeed = new TextField();
                blocksSize = new TextField();
                textSpeed.setText("150");
                blocksSize.setText("30");
                textSpeed.setPrefWidth(110);
                blocksSize.setPrefWidth(110);
                lableSpeed = new Label("Speed");
                colorSnake = new Label("Color Snake");
                colorFood = new Label("Color Food");
                labelBlock = new Label("Blocks size ");

                applyButton = new Button("Apply settings");
                applyButton.setMinWidth(100);
                applyButton.setMinHeight(36);

                VBox vBox2 = new VBox();
                HBox hbox1 = new HBox();
                HBox hbox2 = new HBox();
                HBox hbox3 = new HBox();
                HBox hbox4 = new HBox();

                vBox2.getChildren().add(hbox1);
                hbox1.setSpacing(10);
                vBox2.getChildren().add(hbox2);
                hbox2.setSpacing(10);
                vBox2.getChildren().add(hbox3);
                hbox3.setSpacing(10);
                vBox2.getChildren().add(hbox4);
                hbox4.setSpacing(10);

                hbox1.setPadding(new Insets(15, 20, 10, 10));
                hbox1.getChildren().add(lableSpeed);
                hbox1.getChildren().add(comboBoxSpeed);

                hbox2.setPadding(new Insets(15, 20, 10, 10));
                hbox2.getChildren().add(colorSnake);
                hbox2.getChildren().add(comboBoxColorsSnake);

                hbox3.setPadding(new Insets(15, 20, 10, 10));
                hbox3.getChildren().add(labelBlock);
                hbox3.getChildren().add(comboBoxsizeBlock);

                hbox4.setPadding(new Insets(15, 20, 10, 10));
                hbox4.getChildren().add(colorFood);
                hbox4.getChildren().add(comboBoxColorsFood);

                vBox2.setAlignment(Pos.CENTER);
                vBox2.getChildren().add(applyButton);

                Scene settingsScene = new Scene(vBox2, 230, 250);
                settingsStage.setScene(settingsScene);

                settingsStage.show();

                applyButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {


                        TASK_UPDATE_PERIOD_MS = Integer.parseInt(comboBoxSpeed.getValue().toString());
                        GRID_BLOCK_SIZE = Integer.parseInt(comboBoxsizeBlock.getValue().toString());

                        TASK_UPDATE_DELAY_MS = TASK_UPDATE_PERIOD_MS;
                        // GRID_BLOCK_SIZE = Integer.parseInt(blocksSize.getText());
                        System.out.println(GRID_BLOCK_SIZE);

                        if (comboBoxColorsSnake.getValue() == "Red") {
                            mycolorSnake = Color.RED;
                        }
                        if (comboBoxColorsSnake.getValue() == "Green") {
                            mycolorSnake = Color.GREEN;
                        }
                        if (comboBoxColorsSnake.getValue() == "Blue") {
                            mycolorSnake = Color.BLUE;
                        }
                        if (comboBoxColorsSnake.getValue() == "Pink") {
                            mycolorSnake = Color.PINK;
                        }
                        if (comboBoxColorsSnake.getValue() == "Yellow") {
                            mycolorSnake = Color.YELLOW;
                        }

                        if (comboBoxColorsFood.getValue() == "Red") {
                            mycolorFood = Color.RED;
                        }
                        if (comboBoxColorsFood.getValue() == "Green") {
                            mycolorFood = Color.GREEN;
                        }
                        if (comboBoxColorsFood.getValue() == "Blue") {
                            mycolorFood = Color.BLUE;
                        }
                        if (comboBoxColorsFood.getValue() == "Pink") {
                            mycolorFood = Color.PINK;
                        }
                        if (comboBoxColorsFood.getValue() == "Yellow") {
                            mycolorFood = Color.YELLOW;
                        }

                        grid = new Grid(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
                        snake = new SnakeLogic(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
                        drawGrid();
                        drawSnake();
                        drawFood();
                        settingsStage.close();
                    }

                });
            }
        });

        /*Распознование нажатой клавиши */
        scene.setOnKeyPressed((e) -> {
            if (snake.getTail().size() == 0) {
                if (e.getCode() == KeyCode.UP) {
                    snake.setDirection(UP);
                } else if (e.getCode() == KeyCode.DOWN) {
                    snake.setDirection(DOWN);
                } else if (e.getCode() == KeyCode.LEFT) {
                    snake.setDirection(Direction.LEFT);
                } else if (e.getCode() == KeyCode.RIGHT) {
                    snake.setDirection(Direction.RIGHT);
                } else if (e.getCode() == KeyCode.P) {
                    if (isPaused) {
                        SaveButton.setVisible(false);
                        task = createTimerTask();
                        timer = new Timer("Timer");
                        timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
                        isPaused = false;
                    } else {
                        SaveButton.setVisible(true);
                        timer.cancel();
                        isPaused = true;
                        SaveButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                saveInFile();
                            }

                        });
                    }
                }
            }
            if (snake.getTail().size() > 0) {
                if (e.getCode() == KeyCode.UP && snake.getDirection() != DOWN) {
                    snake.setDirection(UP);
                } else if (e.getCode() == KeyCode.DOWN && snake.getDirection() != UP) {
                    snake.setDirection(DOWN);
                } else if (e.getCode() == KeyCode.LEFT && snake.getDirection() != RIGHT) {
                    snake.setDirection(Direction.LEFT);
                } else if (e.getCode() == KeyCode.RIGHT && snake.getDirection() != LEFT) {
                    snake.setDirection(Direction.RIGHT);
                } else if (e.getCode() == KeyCode.P) {
                    if (isPaused) {
                        task = createTimerTask();
                        timer = new Timer("Timer");
                        timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
                        SaveButton.setVisible(false);
                        isPaused = false;
                    } else {
                        timer.cancel();
                        SaveButton.setVisible(true);
                        isPaused = true;
                        SaveButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                saveInFile();
                            }

                        });
                    }
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        /*Отрисовка сетки, змеи, еды, если игра начитается или вывод результатов игры*/
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (isGameInProgress) {
                    drawGrid();
                    drawSnake();
                    drawFood();
                } else if (isGameOver) {
                    animationTimer.stop();
                    showEndGameAlert();
                    startButton.setVisible(true);
                    exitButton.setVisible(true);
                    settingsButton.setVisible(true);
                    SaveButton.setVisible(true);
                    LoadButton.setVisible(true);

                    grid.reset();
                    snake = new SnakeLogic(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
                    snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
                }
            }
        };
        animationTimer.start();
        task = createTimerTask();
        timer = new Timer("Timer");
        timer.scheduleAtFixedRate(task, TASK_UPDATE_DELAY_MS, TASK_UPDATE_PERIOD_MS);
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void saveInFile() {
        File f = new File("src/save.txt");
        if (f.isFile()) {
            try {
                FileWriter writer = new FileWriter(f, false);
                StringBuffer save = new StringBuffer();
                save.append(TASK_UPDATE_PERIOD_MS);
                String n_save = save.toString() + " ";
                save.setLength(0);
                n_save += "60 ";
                n_save += "60 ";
                save.setLength(0);
                save.append(snake.getTail().size());
                n_save += save.toString();
                n_save += " ";
                save.setLength(0);
                save.append(mycolorSnake);
                n_save += save.toString();
                n_save += " ";
                save.setLength(0);
                save.append(mycolorFood);
                n_save += save.toString();
                n_save += " ";
                save.setLength(0);
                save.append(GRID_BLOCK_SIZE);
                n_save += save.toString();
                n_save += " ";
                save.setLength(0);
                System.out.println(n_save);
                writer.write(n_save);
                writer.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void showAlertWithHeaderText() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Connection");
        alert.setHeaderText("Results:");
        alert.setContentText("Connect to the database successfully!");

        alert.showAndWait();
    }

    /*Отрисовка передвижения змеи, ее увеличения, проверка на столкновение */
    private TimerTask createTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isGameInProgress) {
                    snake.snakeUpdate();

                    if (snake.collidedWithWall()) {
                        endGame();
                    } else if (snake.collidedWithTail()) {
                        endGame();
                    }

                    boolean foundFood = grid.foundFood(snake);
                    if (foundFood) {
                        snake.addTailSegment();
                        grid.addFood();

                    }
                }
            }
        };
        return task;
    }

    /*Конец игры (командная строка)*/
    private void endGame() {
        timer.cancel();
        timer = null;
        isGameInProgress = false;
        isGameOver = true;
    }

    /*Конец игры */
    private void showEndGameAlert() {
        String gameOverText = "Game Over! Score: " + (snake.getTail().size() + 1);
        double textWidth = getTextWidth(gameOverText);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(gameOverText, (WINDOW_WIDTH / 2) - (textWidth / 2), WINDOW_HEIGHT / 2 - 120);
    }

    /*Рисуем сетку*/
    private void drawGrid() {
        graphicsContext.setFill(Color.WHITE); // цвет поля
        graphicsContext.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        graphicsContext.setStroke(Color.LIGHTGRAY); // цвет сетки
        graphicsContext.setLineWidth(0.5);

        for (int x = 0; x < WINDOW_WIDTH; x += GRID_BLOCK_SIZE) {
            graphicsContext.strokeLine(x, 0, x, x + WINDOW_HEIGHT); // веритикальные линии
        }

        for (int y = 0; y < WINDOW_HEIGHT; y += GRID_BLOCK_SIZE) {
            graphicsContext.strokeLine(0, y, y + WINDOW_WIDTH, y);  // горизонтальные линии
        }
    }

    /*Рисуем змею*/
    private void drawSnake() {
        graphicsContext.setFill(mycolorSnake);
        graphicsContext.fillRect(snake.getHeadLocation().getX(), snake.getHeadLocation().getY(), snake.getBlockSize(),
                snake.getBlockSize());
        for (Point tailSegment : snake.getTail()) {
            graphicsContext.fillRect(tailSegment.getX(), tailSegment.getY(), snake.getBlockSize(),
                    snake.getBlockSize());
        }
    }

    /*Рисуем еду*/
    private void drawFood() {
        graphicsContext.setFill(mycolorFood);
        graphicsContext.fillRect(grid.getFood().getLocation().getX(), grid.getFood().getLocation().getY(),
                GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
    }

    /*Узнаем ширину текста, для его размещения по ценру*/
    private double getTextWidth(String string) {
        Text text = new Text(string);
        new Scene(new Group(text));
        text.applyCss();
        return text.getLayoutBounds().getWidth();
    }

    public static void main(String[] args) {
        launch(args);
    }
}