package player;

import javafx.fxml.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestRunner extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent panel = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/test2.fxml"));

        primaryStage.setTitle("Audio Player");
        primaryStage.setResizable(false);
        Scene scene = new Scene(panel, 314, 102);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
