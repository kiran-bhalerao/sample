import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserProfile implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField cpassword;

    @FXML
    private TextField nickName;

    @FXML
    private Text ic_txt;

    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void enterKeyCPassword(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            nickName.requestFocus();
    }

    @FXML
    void enterKeyNickName(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter")) {
            saveData();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void enterKeyPassword(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            cpassword.requestFocus();
    }

    @FXML
    void enterKeyusername(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            password.requestFocus();
    }

    public void saveData() {
        if (!username.getText().equals("") && !password.getText().equals("") && !cpassword.getText().equals("") && !nickName.getText().equals("") && password.getText().equals(cpassword.getText())) {
            try {
                int count = 0;
                System.out.println(Context.getInstance().getUser());
                PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("UPDATE login SET password=? where username=?");
                preparedStatement.setString(1, password.getText());
                preparedStatement.setString(2, Context.getInstance().getUser());
                count += preparedStatement.executeUpdate();
                preparedStatement.close();

                PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("UPDATE login SET username=? where username=?");
                preparedStatement1.setString(1, username.getText());
                preparedStatement1.setString(2, Context.getInstance().getUser());
                count += preparedStatement1.executeUpdate();
                preparedStatement1.close();

                if (count > 0)
                    Context.getInstance().setUser(username.getText());

                //System.out.println(Context.getInstance().getUser());

            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("else");
        }
    }

    @FXML
    void saveClicked(ActionEvent event) {
        saveData();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setText(Context.getInstance().getUser());
        ic_txt.setText(username.getText().substring(0,1).toUpperCase());
        Platform.runLater(() -> password.requestFocus());
    }
}
