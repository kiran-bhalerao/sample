import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowPassword implements Initializable {

    @FXML
    private Label username;

    @FXML
    private Label password;

    @FXML
    private Text ic_txt;

    @FXML
    void close(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setText(Context.getInstance().getUser());
        password.setText(Context.getInstance().getPassword());
        ic_txt.setText(username.getText().substring(0, 1).toUpperCase());

        Context.getInstance().setUser("");
        Context.getInstance().setPassword("");
    }
}
