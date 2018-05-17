import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Signup implements Initializable {


    String name;

    @FXML
    private TextField username;

    @FXML
    private PasswordField ConfirmPassword;

    @FXML
    private JFXButton login_btn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField question;


    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    @FXML
    void enterKeyUsername(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            password.requestFocus();
    }

    @FXML
    void enterKeyConfirm(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            question.requestFocus();
    }


    @FXML
    void enterKeyPassword(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            ConfirmPassword.requestFocus();
    }

    @FXML
    void signClicked(MouseEvent e) {

        boolean hasAlreadUsername = false;
        try {
            PreparedStatement preparedStatement2 = Context.getInstance().getConnection().prepareStatement("SELECT username FROM login");
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            while (resultSet2.next()) {
                hasAlreadUsername = (resultSet2.getString("username").equals(username.getText().toLowerCase()));
                if (hasAlreadUsername)
                    break;

            }
        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }

        if (!hasAlreadUsername && !username.getText().equals("") && !ConfirmPassword.getText().equals("") && password.getText().equals(ConfirmPassword.getText())) {
            try {
                PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("INSERT INTO login VALUES(?,?,?)");
                preparedStatement.setString(1, username.getText().toLowerCase());
                preparedStatement.setString(2, password.getText());
                preparedStatement.setString(3, question.getText());


                preparedStatement.executeUpdate();
                preparedStatement.close();

                Node source = (Node) e.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                stage.setTitle("LOGINme");
                stage.setScene(new Scene(root, 1000, 548));
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }


    }

    @FXML
    void loginBack(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        stage.setTitle("LOGINme");
        stage.setScene(new Scene(root, 1000, 548));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Context.getInstance().setSignup(this);
    }
}
