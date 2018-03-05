package player.utils;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

/**
 * Class for move form when drag on node
 */
public class MoveForm {
    private static double x;
    private static double y;

    private static double initialX;
    private static double initialY;

    /**
     * makes a stage draggable using a given node
     *
     * @param n
     * @param primaryStage
     */
    public static void addDragListeners(final Node n, Stage primaryStage) {

        n.setOnMousePressed((MouseEvent mouseEvent) -> {
            MoveForm.x = n.getScene().getWindow().getX() - mouseEvent.getScreenX();
            MoveForm.y = n.getScene().getWindow().getY() - mouseEvent.getScreenY();
        });

        n.setOnMouseDragged((MouseEvent mouseEvent) -> {
            primaryStage.setX(mouseEvent.getScreenX() + MoveForm.x);
            primaryStage.setY(MoveForm.y + mouseEvent.getScreenY());
        });
    }

    public static void addDragListeners(final Node n) {
        n.setOnMousePressed((MouseEvent me) -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                initialX = me.getSceneX();
                initialY = me.getSceneY();
            } else {
                n.getScene().getWindow().centerOnScreen();
                initialX = n.getScene().getWindow().getX();
                initialY = n.getScene().getWindow().getY();
            }
        });

        n.setOnMouseDragged((MouseEvent me) -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                n.getScene().getWindow().setX(me.getScreenX() - initialX);
                n.getScene().getWindow().setY(me.getScreenY() - initialY);
            }
        });
    }
}
