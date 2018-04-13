package databasegui.MainWindow;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    TableView table;

    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        try{
            Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/tsmith70db", "tsmith70", "Cosc*ey2h");

            ResultSet results = connection.createStatement().executeQuery("select * from EMPLOYEE");
            for(int i = 0; i < results.getMetaData().getColumnCount(); i++){
                final int j = i;
                String columnName = results.getMetaData().getColumnName(i+1);
                TableColumn col = new TableColumn(columnName);
                if(i+1 == 1){
                    col.setEditable(false);
                }
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent event) {
                        String columnName = event.getTablePosition().getTableColumn().getText();
                        
                    }
                });

                table.getColumns().addAll(col);
            }

            while(results.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 0; i < results.getMetaData().getColumnCount(); i++){
                    if(results.getString(i+1) != null){
                        row.add(results.getString(i+1));
                    } else {
                        row.add("");
                    }
                }
                data.add(row);
            }

            table.setItems(data);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
