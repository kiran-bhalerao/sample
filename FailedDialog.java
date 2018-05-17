import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FailedDialog implements Initializable {

    @FXML
    private AnchorPane anchorPan;
    private double xOffset;
    private double yOffset;


    @FXML
    void close(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPan.setOnMousePressed(e -> {
            Node source = (Node) e.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            xOffset = stage.getX() - e.getSceneX();
            yOffset = stage.getY() - e.getSceneY();

            anchorPan.setCursor(Cursor.CLOSED_HAND);
        });

        anchorPan.setOnMouseDragged(event -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setX(event.getSceneX() + xOffset);
            stage.setY(event.getSceneY() + yOffset);
        });

        anchorPan.setOnMouseReleased(event -> {
            anchorPan.setCursor(Cursor.DEFAULT);
        });
    }
}
