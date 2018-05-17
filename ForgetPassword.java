import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgetPassword {

    @FXML
    private TextField username;

    @FXML
    private TextField nickname;

    @FXML
    private Text ic_txt;

    @FXML
    void close(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void enterKeyNickname(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter")) {
            try {
                // GET PASSOWRD FROM DB
                if (!username.getText().equals("") && !nickname.getText().equals("")) {
                    Context.getInstance().setUser(username.getText());
                    getPassFromDB();
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("showPassword.fxml"));
                    stage.setTitle("PASSWORDme");
                    stage.setScene(new Scene(root));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    void enterKeyUsername(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            nickname.requestFocus();
    }

    @FXML
    void keyReleasedUsername(KeyEvent event) {
        try {
            ic_txt.setText(String.valueOf(username.getText().charAt(0)).toUpperCase());
        } catch (Exception e) {
        }
    }

    @FXML
    void getPassword(ActionEvent event) {
        try {
            // GET PASSOWRD FROM DB..
            if (!username.getText().equals("") && !nickname.getText().equals("")) {
                Context.getInstance().setUser(username.getText());
                getPassFromDB();
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("showPassword.fxml"));
                stage.setTitle("PASSWORDme");
                stage.setScene(new Scene(root));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getPassFromDB() {
        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT password FROM login WHERE username=? AND nickname=?");
            preparedStatement.setString(1, username.getText());
            preparedStatement.setString(2, nickname.getText());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Context.getInstance().setPassword(resultSet.getString("password"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
