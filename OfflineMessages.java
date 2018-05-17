import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OfflineMessages implements Initializable{


    @FXML
    private VBox vBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label yourName;

    @FXML
    private Label friendsName;

    @FXML
    private Label yourIp;

    @FXML
    private Label friendsIp;

    @FXML
    private Label friendsNameLabel;

    @FXML
    private Text ic_txt3;

    @FXML
    private Text ic_txt2;

    @FXML
    private Text ic_txt;


    int count;

    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void enterKey(KeyEvent event) {

    }

    @FXML
    void sendClicked(ActionEvent event) {

    }

    @FXML
    void backClicked(MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            stage.setTitle("HOMEme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    public void setMessage(String msg, boolean isSender,int index) {
        try {
            if (!msg.equals("")) {
                if (isSender) {
                    Label label = new Label(msg);
                    label.getStylesheets().add("css/senderBubble.css");
                    label.setId(index + "");

                    label.setOnMouseClicked(event -> {
                        System.out.println(label.getText() + " " + label.getId());
                    });

                    label.setOnContextMenuRequested(event -> {
                        Context.getInstance().setLabel(label);
                        Context.getInstance().setLabelId(Integer.parseInt(label.getId()));
                        try {
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            Parent root;
                            root = FXMLLoader.load(getClass().getResource("deleteMessage.fxml"));
                            Scene scene = new Scene(root);
                            stage.setResizable(false);
                            stage.setScene(scene);
                            stage.showAndWait();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });

                    HBox hBox = new HBox();
                    hBox.getChildren().add(label);
                    hBox.setAlignment(Pos.BASELINE_RIGHT);
                    vBox.getChildren().add(hBox);
                    vBox.setSpacing(10);
                } else {
                    Label label = new Label(msg);
                    label.getStylesheets().add("css/receiverBubble.css");
                    label.setId(index + "");

                    label.setOnMouseClicked(event -> {
                        System.out.println(label.getText() + " " + label.getId());
                    });

                    label.setOnContextMenuRequested(event -> {
                        Context.getInstance().setLabel(label);
                        Context.getInstance().setLabelId(Integer.parseInt(label.getId()));
                        try {
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            Parent root;
                            root = FXMLLoader.load(getClass().getResource("deleteMessage.fxml"));
                            Scene scene = new Scene(root);
                            stage.setResizable(false);
                            stage.setScene(scene);
                            stage.showAndWait();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });

                    HBox hBox = new HBox();
                    hBox.getChildren().add(label);
                    hBox.setAlignment(Pos.BASELINE_LEFT);
                    vBox.getChildren().add(hBox);
                    vBox.setSpacing(10);
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " Error in adding message to labels !");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        yourName.setText(Context.getInstance().getUser());
        yourIp.setText(Context.getInstance().getMyIp());
        friendsName.setText(Context.getInstance().getUsername());
        friendsIp.setText(Context.getInstance().getIP());
        friendsNameLabel.setText(Context.getInstance().getUsername());

        ic_txt.setText(friendsNameLabel.getText().substring(0,1).toUpperCase());
        ic_txt2.setText(yourName.getText().substring(0,1).toUpperCase());
        ic_txt3.setText(friendsName.getText().substring(0,1).toUpperCase());

        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT m_id, message , is_mine FROM messages WHERE f_id in(SELECT f_id FROM friends WHERE ip=? AND name=?) AND username=?");
            preparedStatement.setString(1, Context.getInstance().getIP());
            preparedStatement.setString(2, Context.getInstance().getUsername());
            preparedStatement.setString(3, Context.getInstance().getUser());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                setMessage(resultSet.getString("message"), resultSet.getBoolean("is_mine"),resultSet.getInt("m_id"));
            }

            scrollPane.setVvalue(1.0);

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage() + " error while setting old messages !");
        }
    }
}
