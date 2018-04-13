package databasegui.MainWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow {

    public Stage stage = new Stage();

    public void launch() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainLayout.fxml"));
        stage.setTitle("BlueCross BlueShield");
        Scene scene = new Scene(root, 500, 450);
        //scene.getStylesheets().add("databasegui/LoginWindow/LoginWindow.css");
        stage.setScene(scene);
        stage.show();
    }
}
