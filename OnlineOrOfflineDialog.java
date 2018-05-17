import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class OnlineOrOfflineDialog implements Initializable {

    private boolean isOnline = false;

    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void offlineClicked(ActionEvent event) {
        try {
            Stage stage = Context.getInstance().getHomeStage();
            Parent root = FXMLLoader.load(getClass().getResource("OfflineMessages.fxml"));
            stage.setTitle("PORTme");
            stage.setScene(new Scene(root));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onlineClicked(ActionEvent event) {
        if (isOnline) {
            try {
                Stage stage = Context.getInstance().getHomeStage();
                Parent root = FXMLLoader.load(getClass().getResource("JoinEnterPort.fxml"));
                stage.setTitle("PORTme");
                stage.setScene(new Scene(root));
            } catch (Exception exception) {
                System.out.println(exception.getClass());
            }

            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
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
                    isOnline = true;
                    //Context.getInstance().setMyIp(ipString);
                }
            }
        } catch (SocketException e) {
            System.out.println("get Network Interfaces (IP) exception");
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
