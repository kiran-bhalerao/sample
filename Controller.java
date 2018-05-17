import com.jfoenix.controls.JFXButton;

import com.jfoenix.controls.JFXSnackbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private TextField password;



    @FXML
    private ImageView fb;


    @FXML
    private ImageView insta;


    @FXML
    private AnchorPane loginPane;


    @FXML
    void clicked(MouseEvent e) throws IOException {

        new ProcessBuilder("x-www-browser", "https://www.facebook.com/kiran.nivruttibhalerao").start();

        String launchingMessage;
        if (e.getSource().equals(fb))
            launchingMessage = "Facebook Launching ...";
        else if (e.getSource().equals(insta))
            launchingMessage = "Instagram Launching ...";
        else
            launchingMessage = "Twitter Launching ...";

        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.getScene().getStylesheets().add("myStyles.css");

        JFXSnackbar snackbar = new JFXSnackbar(loginPane);
        snackbar.setPrefWidth(500);
        snackbar.show(launchingMessage, 2000);
    }


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
    void enterKeyPassword(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.getKeyCode("Enter"))
            if (!username.getText().equals("") && !password.getText().equals("") && getLogin(username.getText().toLowerCase(), password.getText())) {

                Context.getInstance().setUser(username.getText().toLowerCase());

                try {
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
                    stage.setTitle("HOMEme");
                    stage.setScene(new Scene(root, 1000, 548));
                } catch (Exception exception) {
                    System.out.println("here is " + exception.getMessage());
                }
            } else {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                Parent root = FXMLLoader.load(getClass().getResource("FailedDialog.fxml"));
                Scene scene = new Scene(root, 410, 255);
                //stage.initStyle(StageStyle.UNDECORATED);
                stage.setResizable(false);
                // scene.getStylesheets().add("/sample/css/FailedDialog.css");
                stage.setScene(scene);
                stage.show();
            }
    }

    private boolean getLogin(String uname, String pass) {
        boolean isLogin = false;
        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT * FROM login");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("username").equals(uname) && resultSet.getString("password").equals(pass)) {
                    isLogin = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return isLogin;
    }

    @FXML
    void signupClicked(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
        stage.setTitle("SIGNUPme");
        stage.setScene(new Scene(root, 1000, 548));
    }

    @FXML
    void forgetPassword(MouseEvent event) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("forgetPassword.fxml"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("FORGETme");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    @FXML
    void loginbtnClicked(ActionEvent event) throws IOException {
        if (!username.getText().equals("") && !password.getText().equals("") && getLogin(username.getText().toLowerCase(), password.getText())) {

            Context.getInstance().setUser(username.getText().toLowerCase());

            try {
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
                stage.setTitle("HOMEme");
                stage.setScene(new Scene(root, 1000, 548));
            } catch (Exception exception) {
                System.out.println("here is " + exception.getMessage());
            }
        } else {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Parent root = FXMLLoader.load(getClass().getResource("FailedDialog.fxml"));
            Scene scene = new Scene(root, 410, 255);
           // stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            // scene.getStylesheets().add("/sample/css/FailedDialog.css");
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Context.getInstance().setUser("");
            Context.getInstance().setPassword("");

            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS login(username text PRIMARY KEY ,password text NOT NULL ,nickname text)");
            preparedStatement.executeUpdate();
            preparedStatement.close();

            PreparedStatement preparedStatement3 = Context.getInstance().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS friends(f_id INTEGER PRIMARY KEY AUTOINCREMENT,ip text NOT NULL , NAME text NOT NULL , username text REFERENCES login(username))");
            preparedStatement3.executeUpdate();
            preparedStatement3.close();

            PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("CREATE TABLE IF not EXISTS messages(m_id INTEGER PRIMARY KEY AUTOINCREMENT,message text,is_mine boolean,f_id INTEGER REFERENCES friends(f_id),username text REFERENCES login(username))");
            preparedStatement1.executeUpdate();
            preparedStatement1.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

