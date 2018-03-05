package player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import player.controllers.MainPlayerController;
import player.controllers.PlayerControllerNext;

public class NextRunner extends Application {
    private Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pStage = primaryStage;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MainPlayerForm2.fxml"));
        //Parent panel = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MainPlayerForm.fxml"));
        Parent panel = loader.load();

        primaryStage.initStyle(StageStyle.UNDECORATED);
        //primaryStage.initStyle(StageStyle.UNIFIED);
        //primaryStage.setTitle("Audio Player");
        //primaryStage.setResizable(false);
        Scene scene = new Scene(panel);
        primaryStage.setScene(scene);

        PlayerControllerNext playerController = loader.getController();
        playerController.setMainRunner(this);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Gets pr stage to Controller.
     * @return
     */
    public Stage getPrimaryStage(){
        return pStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
