import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class About {
    @FXML
    void close(MouseEvent event) {
        Node source= (Node) event.getSource();
        Stage stage= (Stage) source.getScene().getWindow();
        stage.close();
    }
}
