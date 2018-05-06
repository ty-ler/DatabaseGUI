package databasegui.AddWindow;

import databasegui.LoginWindow.LoginWindowController;
import databasegui.MainWindow.MainWindow;
import databasegui.MainWindow.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.prefs.Preferences;

public class AddWindow {
    public Stage stage = new Stage();

    public void launch() throws Exception{

        ResultSet results = null;
        Connection con = null;
        try {
            con = DriverManager.getConnection(LoginWindowController.url, LoginWindowController.username, LoginWindowController.password);
            results = con.prepareStatement("SELECT * FROM " + MainWindowController.selectedTable + ";").executeQuery();
        } catch (Exception e){
            System.out.println(e);
        }

        Parent root = FXMLLoader.load(getClass().getResource("AddWindowLayout.fxml"));
        stage.setTitle("Add " + MainWindowController.selectedTable);
        stage.setResizable(false);
        Scene scene = new Scene(root, 350, results.getMetaData().getColumnCount() * 45);
        Preferences prefs = Preferences.userNodeForPackage(databasegui.Main.class);
        if(prefs.get("dark_mode", "false").equals("false")){
            scene.getStylesheets().add("databasegui/AddWindow/AddWindow.css");
        } else {
            scene.getStylesheets().add("databasegui/AddWindow/AddWindowDark.css");
        }
        stage.setScene(scene);
        stage.show();
        con.close();
    }
}
