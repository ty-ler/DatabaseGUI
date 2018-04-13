package databasegui.MainWindow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    TableView table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/tsmith70db", "tsmith70", "Cosc*ey2h");

            Statement queryStatement = connection.createStatement();
            ResultSet columnResults = queryStatement.executeQuery("select * from INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'EMPLOYEE'");
            ResultSet results = connection.createStatement().executeQuery("select * from EMPLOYEE");
            while (columnResults.next()) {
                String columnName = columnResults.getString("COLUMN_NAME");
                System.out.println(columnName);
                while (results.next()){
                    System.out.println(results.getString(columnName));
                }
                results.beforeFirst();
                table.getColumns().add(new TableColumn<String, String>(columnName));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
