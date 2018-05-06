package databasegui.MainWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class MainWindow {
    public Stage stage = new Stage();

    public void launch() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindowLayout.fxml"));
        stage.setTitle("BlueCross BlueShield");
        Scene scene = new Scene(root, 1024, 720);
        Preferences prefs = Preferences.userNodeForPackage(databasegui.Main.class);
        if (prefs.get("dark_mode", "false").equals("false")) {
            scene.getStylesheets().add("databasegui/MainWindow/MainWindow.css");
        } else {
            scene.getStylesheets().add("databasegui/MainWindow/MainWindowDark.css");
        }
        stage.setScene(scene);
        stage.show();
    }


}
