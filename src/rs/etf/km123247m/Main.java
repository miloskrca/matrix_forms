package rs.etf.km123247m;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("xml/scene.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 300, 275);
        scene.getStylesheets().add("rs/etf/km123247m/css/global.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
