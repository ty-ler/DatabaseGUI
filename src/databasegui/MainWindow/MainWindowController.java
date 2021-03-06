package databasegui.MainWindow;

import com.mysql.jdbc.MysqlDataTruncation;
import databasegui.AddWindow.AddWindow;
import databasegui.LoginWindow.LoginWindowController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainWindowController implements Initializable {

    private String url;
    private String username;
    private String password;
    public static String selectedTable;
    private String autoIncrementColumn;
    private ResultSet columnInfo;
    private ResultSet tables;
    private ResultSet results;
    private ArrayList<String> columnTypes = new ArrayList<>();
    private Boolean darkMode;

    private AddWindow addWindow = new AddWindow();
    private Preferences prefs = Preferences.userNodeForPackage(databasegui.Main.class);

    @FXML
    TableView table;

    @FXML
    ComboBox dropdown;

    @FXML
    Button toggleTheme;

    @FXML
    TextField search;

    private Map<Integer, String> jdbcMappings = getAllJdbcTypeNames();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username = LoginWindowController.username;
        password = LoginWindowController.password;
        url = LoginWindowController.url;

        table.setEditable(true);
        try{
            Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(url, username, password);
            tables = connection.createStatement().executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE';");
            ObservableList<String> tableOptions = FXCollections.observableArrayList();
            while(tables.next()){
                String tableName = tables.getString("TABLE_NAME");
                tableOptions.add(tableName);
            }
            tables.beforeFirst();
            dropdown.getItems().addAll(tableOptions);
            tables.next();
            selectedTable = tables.getString("TABLE_NAME");
            dropdown.setValue(selectedTable);
            dropdown.valueProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    selectedTable = newValue.toString();
                    refreshTable();
                }
            });
            refreshTable();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    public void insertNewRow(){
        try{
            addWindow.launch();
            addWindow.stage.setOnHiding(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    refreshTableData();
                }
            });
        } catch (Exception e){
            System.out.println(e);
        }
    }

    @FXML
    public void deleteRow(){
        if(table.getSelectionModel().getSelectedItem() != null) {
            String row = table.getSelectionModel().getSelectedItem().toString();
            row = row.replace("[", "");
            row = row.replace("]", "");
            System.out.println(row);
            String[] rowValues = row.split(",");

            try{
                Connection con = DriverManager.getConnection(LoginWindowController.url, LoginWindowController.username, LoginWindowController.password);
                con.prepareStatement("DELETE FROM " + MainWindowController.selectedTable + " WHERE `" + autoIncrementColumn + "` = '" + rowValues[0] + "';").executeUpdate();
                con.close();
                refreshTableData();
            } catch (Exception e){

            }
        }

    }

    @FXML
    public void toggleTheme(){
        Scene scene =  toggleTheme.getScene();
        if(prefs.get("dark_mode", "false").equals("false")){
            darkMode = true;
            prefs.put("dark_mode", "true");
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("MainWindowDark.css").toExternalForm());
        } else {
            darkMode = false;
            prefs.put("dark_mode", "false");
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("MainWindow.css").toExternalForm());
        }
    }

    public void refreshTable(){
        table.getColumns().clear();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            results = connection.createStatement().executeQuery("select * from " + selectedTable);
            columnInfo = connection.createStatement().executeQuery("SELECT COLUMN_NAME, DATA_TYPE, COLUMN_KEY, EXTRA \n" +
                    "FROM INFORMATION_SCHEMA.COLUMNS \n" +
                    "WHERE TABLE_NAME = '" + selectedTable + "';");
            columnTypes.clear();
            for (int i = 0; i < results.getMetaData().getColumnCount(); i++) {
                int columnWidth = results.getMetaData().getColumnCount();
                columnInfo.next();
                columnTypes.add(jdbcMappings.get(results.getMetaData().getColumnType(i+1)));
                final int j = i;
                String columnName = results.getMetaData().getColumnName(i + 1);
                TableColumn col = new TableColumn(columnName);
                if (columnInfo.getString("EXTRA").equals("auto_increment")) {
                    autoIncrementColumn = columnName;
                    col.setEditable(false);
                }
                if (columnInfo.getString("COLUMN_KEY").equals("PRI")) {
                    col.getStyleClass().add("italic");
                }

                if (columnInfo.getString("DATA_TYPE").equals("int")) {

                    col.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, Number>, ObservableValue<Number>>() {
                        public ObservableValue<Number> call(TableColumn.CellDataFeatures<ObservableList, Number> param) {
                            return new SimpleIntegerProperty(Integer.parseInt(param.getValue().get(j).toString()));
                        }
                    });
                } else {
                    col.setCellFactory(TextFieldTableCell.forTableColumn());
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }

                col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent event) {
                        String columnName = event.getTablePosition().getTableColumn().getText();
                        String query = "UPDATE " + selectedTable + " SET " + columnName + "='" + event.getNewValue() + "' WHERE " + columnName + "='" + event.getOldValue() + "';";
                        System.out.println(query);
                        String columnType = columnTypes.get(event.getTablePosition().getColumn());
                        try {
                            Connection con = DriverManager.getConnection(url, username, password);
                            con.prepareStatement(query).executeUpdate();
                            con.close();
                            refreshTableData();
                        } catch (MysqlDataTruncation e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("MySQL Error");
                            alert.setHeaderText(null);
                            alert.setContentText(event.getNewValue() + " is an invalid value for column " + columnName + "(" + columnType + ")");
                            alert.showAndWait();
                            System.out.println(e);
                            refreshTable();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                col.prefWidthProperty().bind(table.widthProperty().divide(columnWidth));
                table.getColumns().addAll(col);
            }

            columnInfo.beforeFirst();

            refreshTableData();

            connection.close();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void refreshTableData(){
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        try{
            Connection con = DriverManager.getConnection(url, username, password);
            results = con.createStatement().executeQuery("select * from " + selectedTable);
            while(results.next()){
                ObservableList row = FXCollections.observableArrayList();
                for(int i = 0; i < results.getMetaData().getColumnCount(); i++){
                    if(results.getString(i+1) != null){
                        row.add(results.getString(i+1));
                    } else {
                        row.add("");
                    }
                }
                data.add(row);
            }
            results.beforeFirst();
            con.close();
            table.setItems(data);
            table.refresh();

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public Map<Integer, String> getAllJdbcTypeNames() {

        Map<Integer, String> result = new HashMap<Integer, String>();

        for (Field field : Types.class.getFields()) {
            try{
                result.put((Integer)field.get(null), field.getName());
            } catch (Exception e){
                System.out.println(e);
            }
        }

        return result;
    }
}
