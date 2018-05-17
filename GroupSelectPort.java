import com.jfoenix.controls.JFXButton;
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class GroupSelectPort implements Initializable {

    @FXML
    private JFXButton port1btn;

    @FXML
    private JFXButton port2btn;

    @FXML
    private JFXButton port3btn;

    @FXML
    private JFXButton port4btn;

    @FXML
    private JFXButton port6btn;

    @FXML
    private JFXButton port7btn;

    @FXML
    private JFXButton port5btn;

    @FXML
    private Label myIp;

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
    void portBtnClicked(ActionEvent event) {
        if (event.getSource().equals(port1btn))
            Context.getInstance().setPORT(port1btn.getText());

        if (event.getSource().equals(port2btn))
            Context.getInstance().setPORT(port2btn.getText());

        if (event.getSource().equals(port3btn))
            Context.getInstance().setPORT(port3btn.getText());

        if (event.getSource().equals(port4btn))
            Context.getInstance().setPORT(port4btn.getText());

        if (event.getSource().equals(port5btn))
            Context.getInstance().setPORT(port5btn.getText());

        if (event.getSource().equals(port6btn))
            Context.getInstance().setPORT(port6btn.getText());

        if (event.getSource().equals(port7btn))
            Context.getInstance().setPORT(port7btn.getText());

        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("group.fxml"));
            stage.setTitle("GROUPme");
            stage.setScene(new Scene(root, 1000, 548));
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
        port1btn.setText(String.valueOf(getRandom()));
        port2btn.setText(String.valueOf(getRandom()));
        port3btn.setText(String.valueOf(getRandom()));
        port4btn.setText(String.valueOf(getRandom()));
        port4btn.setText(String.valueOf(getRandom()));
        port5btn.setText(String.valueOf(getRandom()));
        port6btn.setText(String.valueOf(getRandom()));
        port7btn.setText(String.valueOf(getRandom()));
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

    public int getRandom() {
        int rand = (int) (Math.random() * 100000);
        if (rand > 65635)
            rand = rand - rand / 2;
        if (rand < 9000)
            rand = rand + 9000;
        return rand;
    }
}
