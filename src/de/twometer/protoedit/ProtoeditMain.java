package de.twometer.protoedit;

import de.twometer.protoedit.ui.MainController;
import de.twometer.protoedit.util.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProtoeditMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ResourceLoader.getResource("layout/main.fxml"));
        Scene scene = new Scene(loader.load());

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Protodesign Editor v1.0.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
