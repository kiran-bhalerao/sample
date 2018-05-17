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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class Join implements Initializable {

    Stage JoinStage;

    @FXML
    private VBox vBox;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ScrollPane scrollPane;


    @FXML
    private Label myIp;


    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void aboutClicked(MouseEvent event) {
        try {
            Stage abStage=new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("about.fxml"));
            abStage.setTitle("ABOUTme");
            abStage.setScene(new Scene(root));
            abStage.show();
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    @FXML
    void backClicked(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            stage.setTitle("CHATme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    public void setLabelData(String name, String ip) {

        try {
            Label label = new Label(name);
            label.getStylesheets().add("css/addListLabel.css");
            label.setId(ip);

            label.setOnMouseClicked(event -> {
                Context.getInstance().setIP(ip);
                Context.getInstance().setUsername(name);

                try {
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("JoinEnterPort.fxml"));
                    stage.setTitle("PORTme");
                    stage.setScene(new Scene(root, 1000, 548));
                } catch (Exception exception) {
                    System.out.println(exception.getClass());
                }
            });

            label.setOnContextMenuRequested(event -> {
                //isContextMenuClicked = false;

                Context.getInstance().setFromStageTODelete("join");
                Context.getInstance().setDeleteLabelName(label.getText());
                Context.getInstance().setHomeStage((Stage) ((Node) event.getSource()).getScene().getWindow());

                try {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    Parent root;
                    root = FXMLLoader.load(getClass().getResource("deleteHistoryLabel.fxml"));
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
            hBox.setAlignment(Pos.CENTER);
            hBox.setStyle("-fx-background-color: rgba(0,168,355,0)");
            vBox.getChildren().add(hBox);
            vBox.setSpacing(20);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void addFriendCLicked(ActionEvent event) throws IOException {

        JoinStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Context.getInstance().setJoinStage(JoinStage);

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("addFriendDialog.fxml"));
        stage.setTitle("ADDme");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Enumeration<NetworkInterface> nets;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                String res = displayInterfaceInformation(netint);
                if (!res.equals("")) {
                    Context.getInstance().setMyIp(res);
                }
            }
        } catch (SocketException e) {
            System.out.println("get Network Interfaces (IP) exception");
        }


        Context.getInstance().setJoin(this);
        myIp.setText(Context.getInstance().getMyIp());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends");
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                //Context.getInstance().getJoin().
                setLabelData(resultSet.getString("name"), resultSet.getString("ip"));
            }
            resultSet.close();
            preparedStatement1.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    static String displayInterfaceInformation(NetworkInterface netint) {
        String resultString = "";
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            String ip = inetAddress.toString().substring(1, 4);
            if (ip.equals("192")) {
                resultString = inetAddress.toString().substring(1);
            }
        }
        return resultString;
    }

}
