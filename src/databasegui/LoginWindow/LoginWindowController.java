package databasegui.LoginWindow;

import com.mysql.jdbc.CommunicationsException;
import databasegui.MainWindow.MainWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.security.Key;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginWindowController implements Initializable{

    public static String url;
    public static String username = "wzhang9";
    public static String password = "Cosc*wctm";

    public MainWindow mainWindow = new MainWindow();

    @FXML
    TextField usernameField,passwordField;

    @FXML
    Button loginButton;
    @FXML
    Label msg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        url = "jdbc:mysql://triton.towson.edu:3360/" + username + "db";
        try{
            mainWindow.launch();
        } catch (Exception e){
            System.out.println(e);
        }

        usernameField.setOnAction(event -> {
            login();
        });

        passwordField.setOnAction(event -> {
            login();
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
    public void loginAction(javafx.event.ActionEvent event) {
        login();
    }

    public void login(){
//        username = usernameField.getText();
//        password = passwordField.getText();
        url = "jdbc:mysql://triton.towson.edu:3360/" + username + "db";
        try {
            Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(url, username, password);// Please use your database name here

            Stage stage = (Stage) loginButton.getScene().getWindow();

            stage.close();

            mainWindow.launch();
        }
        catch(SQLException e){
            System.out.println(e);
            msg.setText("Invalid username/password");
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
