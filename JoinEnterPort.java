import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class JoinEnterPort implements Initializable {


    @FXML
    private TextField enterPortTextField;

    private String port;


    @FXML
    private Label myIp;

    @FXML
    void backClicked(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("join.fxml"));
            stage.setTitle("JOINme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
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
    void close(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void startPortBtn(ActionEvent event) {
        if (!(enterPortTextField.getText()).equals("")) {
            port = enterPortTextField.getText();
            Context.getInstance().setPORT(port);

            try {
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("chatting.fxml"));
                stage.setTitle("MESSAGEme");
                stage.setScene(new Scene(root, 1000, 548));
            } catch (Exception exception) {
                System.out.println(exception.getClass());
            }
        }
    }


    // if (event.getCode() == KeyCode.getKeyCode("Enter"))

    @FXML
    void enterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.getKeyCode("Enter")) {
            if (!(enterPortTextField.getText()).equals("")) {
                port = enterPortTextField.getText();
                Context.getInstance().setPORT(port);

                try {
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("chatting.fxml"));
                    stage.setTitle("MESSAGEme");
                    stage.setScene(new Scene(root, 1000, 548));
                } catch (Exception exception) {
                    System.out.println(exception.getClass());
                }
            }
        }
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


        myIp.setText(Context.getInstance().getMyIp());

        Platform.runLater(() -> enterPortTextField.requestFocus());

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
