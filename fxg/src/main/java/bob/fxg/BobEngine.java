package bob.fxg;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class BobEngine extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage theStage) {
        theStage.setTitle("JavaFx Game, Version 0.1");

        Group root = new Group();
        Scene theScene = new Scene(root);
        theStage.setScene(theScene);

        BobContext context = new BobContext();

        Canvas canvas = new Canvas(context.WIDTH, context.HEIGHT);
        root.getChildren().add(canvas);

        List<String> input = new ArrayList<>();

        theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String code = e.getCode().toString();

                // only add once... prevent duplicates
                if (!input.contains(code)) {
                    input.add(code);
                }
            }
        });

        theScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String code = e.getCode().toString();
                input.remove(code);
            }
        });

        theScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                context.mouse.setX(e.getX());
                context.mouse.setY(e.getY());
            }
        });

        GraphicsContext gc = canvas.getGraphicsContext2D();
        reset(gc);

        BobSketch sketch = new BobSketch();
        sketch.setup(context);

        // Image earth = new Image("earth.png");

        final long startNanoTime = System.nanoTime();

        new AnimationTimer() {
            public void handle(long currentNanoTime) {                
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                                
                gc.clearRect(0, 0, context.WIDTH, context.HEIGHT);
                
                // background image clears canvas
                // gc.drawImage(earth, x, y);
                
                if (!context.pause) {
                    sketch.update(context);
                }
                sketch.draw(context, gc);

                reset(gc);

                // Rahmen
                gc.strokeRect(0, 0, context.WIDTH, context.HEIGHT);


                // Fußzeile
                StringBuffer footer = new StringBuffer();
                footer.append("Pause: ").append(context.pause ? "ON" : "OFF");
                footer.append("  ");
                footer.append("Debug: ").append(context.debug ? "ON" : "OFF");
                gc.fillText(footer.toString(), 10, context.HEIGHT - 10);
                // gc.strokeText(footer, 10, context.HEIGHT - 10);

                if (input.contains("P")) {
                    context.pause = !context.pause;
                    input.remove("P");
                }

            }
        }.start();

        theStage.show();
    }

    private void reset(GraphicsContext gc) {
        Font theFont = Font.font( "Helvetica", FontWeight.BOLD, 16);
        gc.setFont( theFont );
        gc.setFill( Color.GRAY);
        gc.setStroke( Color.LIGHTGREY);
        gc.setLineWidth(1);
    }

}