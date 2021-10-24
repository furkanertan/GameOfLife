package gameoflife;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static final int width = 500;
    private static final int height = 500;
    private static final int cellSize = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        Scene scene = new Scene(root, width, height);
        final Canvas canvas = new Canvas(width, height);

        Button run = new Button("Run");
        run.setStyle("-fx-background-color: Green");
        Button stop = new Button("Stop");
        stop.setStyle("-fx-background-color: Red");

        root.getChildren().addAll(canvas, new HBox(10, run, stop));
        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();

        int rows = (int) Math.floor(height / cellSize);
        int cols = (int) Math.floor(width / cellSize);

        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.getFill();
        GameofLife gameofLife = new GameofLife(rows, cols, graphics);
        gameofLife.init();

        AnimationTimer runAnimation = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // only update once every second
                if ((now - lastUpdate) >= TimeUnit.MILLISECONDS.toNanos(1000)) {
                    gameofLife.checkForNext();
                    lastUpdate = now;
                }
            }
        };

        run.setOnAction(  l -> runAnimation.start());
        stop.setOnAction( l -> runAnimation.stop());
    }



    private static class GameofLife {
        private final int rows;
        private final int cols;
        private int[][] grid;
        private Random random = new Random();
        private final GraphicsContext graphics;

        public GameofLife(int rows, int cols, GraphicsContext graphics) {
            this.rows = rows;
            this.cols = cols;
            this.graphics = graphics;
            grid = new int[rows][cols];
        }

        public void init() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = 0;
                }
            }
            for (int k = 0; k < 500; k++) {
                grid[random.nextInt(rows)][random.nextInt(cols)] = 1;
            }
            draw();
        }

        private void draw() {
            // clear graphics
            graphics.setFill(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == 1) {
                        // first rect will end up becoming the border
                        graphics.setFill(Color.gray(0.5, 0.5));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                        graphics.setFill(Color.BLACK);
                        graphics.fillRect((i * cellSize) + 1, (j * cellSize) + 1, cellSize - 2, cellSize - 2);
                    }else {
                        graphics.setFill(Color.gray(0.5, 0.5));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                        graphics.setFill(Color.WHITE);
                        graphics.fillRect((i * cellSize) + 1, (j * cellSize) + 1, cellSize - 2, cellSize - 2);
                    }
                }
            }
        }

        private int calculateAliveNeighbors(int x, int y) {
            int total = 0;
            for (int k = -1; k < 2; k++) {
                for (int l = -1; l <2 ; l++) {
                    total += grid[((x + k) + rows) % rows][((l + y) + cols) % cols];
                }
            }
            total -= grid[x][y];
            return total;
        }

        public void checkForNext() {
            int[][] next = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int neighbors = calculateAliveNeighbors(i, j);
                    if (neighbors == 3) {
                        next[i][j] = 1;
                    }else if (neighbors < 2 || neighbors > 3) {
                        next[i][j] = 0;
                    }else {
                        next[i][j] = grid[i][j];
                    }
                }
            }
            grid = next;
            draw();
        }
    }
}