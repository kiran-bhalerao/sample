import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;

public class AddFriendDialog implements Initializable {

    @FXML
    private TextField fullNameTxt;

    @FXML
    private TextField ipTxt;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private Text ic_txt;

    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void deleteData(ActionEvent event) {
        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("DELETE FROM friends WHERE name=? AND username=?");
            preparedStatement.setString(1, fullNameTxt.getText());
            preparedStatement.setString(2, Context.getInstance().getUser());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            Stage stage = Context.getInstance().getJoinStage();
            Parent root = FXMLLoader.load(getClass().getResource("join.fxml"));
            stage.setTitle("JOINme");
            stage.setScene(new Scene(root, 1000, 548));

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    void saveData(ActionEvent event) throws SQLException {

        try {
            if (!fullNameTxt.getText().equals("") && (ipTxt.getText().substring(0, 8)).equals("192.168.")) {

                boolean notPresent = true;
                PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends WHERE username=?");
                preparedStatement1.setString(1, Context.getInstance().getUser());
                ResultSet resultSet = preparedStatement1.executeQuery();
                while (resultSet.next()) {
                    if (!resultSet.getString("name").equals(fullNameTxt.getText()) || !resultSet.getString("ip").equals(ipTxt.getText())) {
                        notPresent = true;
                    } else {
                        notPresent = false;
                        break;
                    }
                }

                if (notPresent) {

                    try {
                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("INSERT INTO friends(ip,name,username) values(?,?,?)");
                        preparedStatement.setString(1, ipTxt.getText());
                        preparedStatement.setString(2, fullNameTxt.getText());
                        preparedStatement.setString(3, Context.getInstance().getUser());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    Context.getInstance().getJoin().setLabelData(fullNameTxt.getText(), ipTxt.getText());
                }

                resultSet.close();
                preparedStatement1.close();

                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void enterKeyFullName(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            ipTxt.requestFocus();
    }


    @FXML
    void keyReleaseUsername(KeyEvent event) {
        try {
            ic_txt.setText(String.valueOf(fullNameTxt.getText().charAt(0)).toUpperCase());
        } catch (Exception e) {
        }
    }

    @FXML
    void enterKeyIP(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter")) {
            try {
                if (!fullNameTxt.getText().equals("") && (ipTxt.getText().substring(0, 8)).equals("192.168.")) {
                    boolean notPresent = false;
                    PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends WHERE username=?");
                    preparedStatement1.setString(1, Context.getInstance().getUser());
                    ResultSet resultSet = preparedStatement1.executeQuery();
                    while (resultSet.next()) {
                        if (resultSet.getString("name").equals(fullNameTxt.getText()) && resultSet.getString("ip").equals(ipTxt.getText())) {
                            notPresent = false;
                            break;
                        } else {
                            notPresent = true;
                        }
                    }

                    if (notPresent) {

                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("INSERT INTO friends(ip,name,username) values(?,?,?)");
                        preparedStatement.setString(1, ipTxt.getText());
                        preparedStatement.setString(2, fullNameTxt.getText());
                        preparedStatement.setString(3, Context.getInstance().getUser());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();

                        Context.getInstance().getJoin().setLabelData(fullNameTxt.getText(), ipTxt.getText());
                    }

                    resultSet.close();
                    preparedStatement1.close();

                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}