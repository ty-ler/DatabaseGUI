package databasegui.AddWindow;

import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import databasegui.LoginWindow.LoginWindowController;
import databasegui.MainWindow.MainWindowController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class AddWindowController implements Initializable {

    @FXML
    GridPane grid;

    @FXML
    ScrollPane scrollRoot;

    private ResultSet results;
    private Connection con;
    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<Node> textFields = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(grid.getScene());
        try{
            con = DriverManager.getConnection(LoginWindowController.url, LoginWindowController.username, LoginWindowController.password);
            results = con.prepareStatement("SELECT * FROM " + MainWindowController.selectedTable).executeQuery();

            for(int i = 0; i < results.getMetaData().getColumnCount(); i++){
                System.out.println(results.getMetaData().getColumnName(i+1));
                columns.add(results.getMetaData().getColumnName(i+1));
            }
        } catch (Exception e){
            System.out.println(e);
        }

        for(int i = 0; i < columns.size(); i++) {
            Label label = new Label(columns.get(i) + ":");
            grid.add(label, 0, i);
            try{
                if(results.getMetaData().getColumnType(i+1) == Types.DATE){
                    DatePicker datePicker = new DatePicker();
                    datePicker.setEditable(false);
                    datePicker.setConverter(new StringConverter<LocalDate>() {
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        @Override
                        public String toString(LocalDate date) {
                            if(date != null) {
                                return dateFormatter.format(date);
                            } else {
                                return "";
                            }
                        }

                        @Override
                        public LocalDate fromString(String string) {
                            if(string != null && !string.isEmpty()){
                                return LocalDate.parse(string, dateFormatter);
                            } else {
                                return null;
                            }
                        }
                    });

                    textFields.add(datePicker);
                    grid.add(datePicker, 1, i);

                } else {
                    TextField textField = new TextField();
                    textFields.add(textField);

                    grid.add(textField, 1, i);
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        Button cancelButton = new Button("Cancel");
        Button confirmButton = new Button("Confirm");
        cancelButton.setOnAction(event -> {
            cancel();
        });
        confirmButton.setOnAction(event -> {
            confirm();
        });
        hBox.getChildren().add(cancelButton);
        hBox.getChildren().add(confirmButton);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(hBox, 0, columns.size(), 2, 1);
    }

    @FXML
    public void confirm(){
        String query = "INSERT INTO `" + MainWindowController.selectedTable + "` VALUES (";
        Stage stage = (Stage) grid.getScene().getWindow();
        for(int i = 0; i < columns.size(); i++){
            if(i+1 == columns.size()){
                query += "?);";
            } else {
                query += "?, ";
            }
        }
        try {
            Border errorBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
            PreparedStatement statement = con.prepareStatement(query);
            System.out.println(query);
            for(int i = 0; i < columns.size(); i++){
                if(textFields.get(i) instanceof TextField){
                    TextField textField = (TextField) textFields.get(i);

                    if(!textField.getText().isEmpty()){
                        if(results.getMetaData().getColumnType(i+1) == Types.INTEGER){
                            try{
                                int input = Integer.parseInt(((TextField) textFields.get(i)).getText());
                                statement.setInt(i+1, input);
                            } catch (NumberFormatException e){
                                ((TextField) textFields.get(i)).setBorder(errorBorder);
                            }

                        } else {
                            String input = ((TextField) textFields.get(i)).getText();
                            statement.setString(i+1, input);
                        }
                    } else {
                        textField.setBorder(errorBorder);
                    }


                } else if(textFields.get(i) instanceof DatePicker){
                    DatePicker picker = (DatePicker) textFields.get(i);

                    try{
                        statement.setString(i+1, picker.getValue().toString());
                    } catch (NullPointerException e){
                        picker.setBorder(errorBorder);
                    }
                }
            }
            statement.executeUpdate();
            con.close();
            stage.close();
        } catch (MysqlDataTruncation e) {
            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    public void cancel(){
        try{
            con.close();
        } catch (Exception e){
            System.out.println(e);
        }
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }
}
