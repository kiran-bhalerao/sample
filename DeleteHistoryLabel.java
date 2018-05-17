import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class DeleteHistoryLabel implements Initializable{

    @FXML
    private Label deleteHeading;

    @FXML
    private Label deleteSubHeading;

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
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("DELETE FROM friends WHERE name=? AND username=?");
            preparedStatement.setString(1, Context.getInstance().getDeleteLabelName());
            preparedStatement.setString(2, Context.getInstance().getUser());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            String title=Context.getInstance().getFromStageTODelete();
            String res=Context.getInstance().getFromStageTODelete()+".fxml";

            Stage stage = Context.getInstance().getHomeStage();
            Parent root = FXMLLoader.load(getClass().getResource(res));
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1000, 548));

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteHeading.setText("Delete "+Context.getInstance().getDeleteLabelName());
        deleteSubHeading.setText("Do you really want to delete "+Context.getInstance().getDeleteLabelName()+" ..!");
    }
}
