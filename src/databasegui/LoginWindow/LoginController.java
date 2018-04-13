package databasegui.LoginWindow;

import databasegui.MainWindow.MainWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.security.Key;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable{
    @FXML
    TextField usernameField,passwordField;

    @FXML
    Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameField.setOnAction(event -> {

        });

        usernameField.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if(event.getCode() == KeyCode.TAB){
                passwordField.setFocusTraversable(true);
            }
        });

        passwordField.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if(event.getCode() == KeyCode.TAB && event.isShiftDown()){
                usernameField.setFocusTraversable(true);
            }
        });
    }

    @FXML
    public void loginAction(javafx.event.ActionEvent event) throws Exception{
        login();
    }

    public void login() throws Exception{
        String username = usernameField.getText();
        String password = passwordField.getText();

        Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/" + username + "db", username, password);// Please use your database name here
            PreparedStatement updateStaff;
            Statement queryStatement = connection.createStatement();
            updateStaff = null;
            String querys = "select * from EMPLOYEE;";
            ResultSet results = queryStatement.executeQuery(querys);

            Stage stage = (Stage) loginButton.getScene().getWindow();

            stage.close();

            MainWindow mainWindow = new MainWindow();
            mainWindow.launch();
        }
        catch(Exception e){
            System.out.println(e);
        }
//        querys="UPDATE wzhang9db.EMPLOYEE SET salary = 8802 WHERE SSN = '123456789';";
//        updateStaff = connection.prepareStatement(querys);
    }

}
