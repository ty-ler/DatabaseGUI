package databasegui.AddEmpWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddEmpWindow {
    public Stage stage = new Stage();

    public void launch() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("AddEmployeeWindowLayout.fxml"));
        stage.setTitle("BlueCross BlueShield");
        Scene scene = new Scene(root, 1024, 720);
        scene.getStylesheets().add("databasegui/AddEmpWindow/AddEmpWindow.css");
        stage.setScene(scene);
        stage.show();
    }
}
