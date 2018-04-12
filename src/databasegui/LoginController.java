package databasegui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.xml.soap.Text;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;



import java.awt.event.ActionEvent;


public class LoginController {
    @FXML
    TextField usernameField,passwordField;
    @FXML
    public void login(javafx.event.ActionEvent event) throws ClassNotFoundException,InstantiationException, IllegalAccessException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Object newInstance;
        newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/wzhang9db", username, password);// Please use your database name here
            PreparedStatement updateStaff;
            Statement queryStatement = connection.createStatement();
            updateStaff = null;
            String querys = "select * from EMPLOYEE;";
            ResultSet results = queryStatement.executeQuery(querys);
            while (results.next()) {
                System.out.print(results.getString("fName"));
                System.out.print("    ");
                System.out.print(results.getString("lName"));
                System.out.println();
            }
        }
        catch(Exception e){
            System.out.println("Wrong Input");
        }
//        querys="UPDATE wzhang9db.EMPLOYEE SET salary = 8802 WHERE SSN = '123456789';";
//        updateStaff = connection.prepareStatement(querys);

    }

}
