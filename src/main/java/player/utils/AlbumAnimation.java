package player.utils;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Class for animation Album Image on Main Player Form
 */
public class AlbumAnimation {
    public static SequentialTransition fadeTransition(final ImageView iv, final Image img) {
        FadeTransition fadeOutTransition
                = new FadeTransition(Duration.seconds(0.4), iv);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.3);
        fadeOutTransition.setOnFinished((ActionEvent arg0) -> {
            iv.setImage(img);
        });

        FadeTransition fadeInTransition
                = new FadeTransition(Duration.seconds(0.2), iv);
        fadeInTransition.setFromValue(0.3);
        fadeInTransition.setToValue(1.0);
        SequentialTransition sequentialTransition
                = SequentialTransitionBuilder
                .create()
                .children(fadeOutTransition, fadeInTransition)
                .build();

        return sequentialTransition;
    }

    public static SequentialTransition translateTransition(final ImageView iv, final Image img, int fromX, int toY) {
        TranslateTransition translateOutTransition
                = new TranslateTransition(Duration.seconds(0.4), iv);
        translateOutTransition.setFromX(fromX);
        translateOutTransition.setToX(toY);
        translateOutTransition.setOnFinished((ActionEvent arg0) -> {
            iv.setImage(img);
        });

        TranslateTransition translateInTransition
                = new TranslateTransition(Duration.seconds(0.4), iv);
        translateInTransition.setFromX(toY);
        translateInTransition.setToX(fromX);
        SequentialTransition sequentialTransition
                = SequentialTransitionBuilder
                .create()
                .children(translateOutTransition, translateInTransition)
                .build();

        return sequentialTransition;
    }
}
