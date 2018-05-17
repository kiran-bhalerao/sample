import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.PreparedStatement;

public class DeleteMessage {


    @FXML
    void close(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void noClicked(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void yesClicked(ActionEvent event) {
        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("UPDATE messages SET message=? WHERE m_id=?");
            preparedStatement.setString(1, "! This Message was Deleted.");
            preparedStatement.setInt(2, Context.getInstance().getLabelId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            Context.getInstance().getLabel().setText("! This Message was Deleted.");

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
