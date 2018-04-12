package databasegui.LoginWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("LoginLayout.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 500, 450);
        scene.getStylesheets().add("databasegui/LoginWindow/LoginWindow.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void launchApp(String[] args){
        launch(args);
    }
}
