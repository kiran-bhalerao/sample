import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class Group implements Initializable {

    private String message = "";
    private DatagramSocket ds;

    private LinkedHashSet<String> linkedHashSet;
    private Iterator<String> iterator;

    @FXML
    private VBox vBox;

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
            Parent root = FXMLLoader.load(getClass().getResource("GroupSelectPort.fxml"));
            stage.setTitle("SELECTme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
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


        try {
            ds = new DatagramSocket(Integer.parseInt(Context.getInstance().getPORT()));
        } catch (SocketException e) {
            e.printStackTrace();
        }


        myIp.setText(Context.getInstance().getMyIp() + " : " + Context.getInstance().getPORT());

        linkedHashSet = new LinkedHashSet<>();

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        new ReceiverThread().start();
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

    public class ReceiverThread extends Thread {
        public ReceiverThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                System.out.println("inside ReceiverThread");
                while (true) {
                    byte[] receivePKT = new byte[1024];
                    DatagramPacket dp1 = new DatagramPacket(receivePKT, receivePKT.length);
                    ds.receive(dp1);
                    message = new String(dp1.getData());
                    InetAddress inetAddress = dp1.getAddress();

                    Platform.runLater(() -> {

                        try {
                            System.out.println(message);
                            System.out.println("username " + Context.getInstance().getUsername());
                            System.out.println("user " + Context.getInstance().getUser());
                            System.out.println("rec msg username " + message.substring((message.indexOf("~|}{`") + 5), dp1.getLength()));
                            System.out.println("rec msg user " + message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))));

                        }catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        //if (message.substring((message.indexOf("~|}{`") + 5), dp1.getLength()).equals(Context.getInstance().getUsername())) {

                            try {
                                boolean isInList = false;
                                iterator = linkedHashSet.iterator();
                                while (iterator.hasNext()) {
                                    isInList = String.valueOf(inetAddress).equals((String) iterator.next());
                                    if (isInList)
                                        break;
                                }
                                if (isInList) {

                                    //Add msg to database....

                                    try {
                                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT f_id from friends where ip=? AND name=? AND username=?");
                                        preparedStatement.setString(1, String.valueOf(inetAddress).substring(1));
                                        preparedStatement.setString(2, message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))));
                                        preparedStatement.setString(3, Context.getInstance().getUser());

                                        ResultSet resultSet = preparedStatement.executeQuery();
                                        resultSet.next();
                                        int f_id = resultSet.getInt("f_id");
                                        resultSet.close();
                                        preparedStatement.close();

                                        PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("INSERT INTO messages(message,is_mine,f_id,username) values(?,?,?,?)");
                                        preparedStatement1.setString(1, message.substring(0, message.indexOf("#-^.$")));
                                        preparedStatement1.setBoolean(2, false);
                                        preparedStatement1.setInt(3, f_id);
                                        preparedStatement1.setString(4, Context.getInstance().getUser());

                                        preparedStatement1.executeUpdate();
                                        preparedStatement1.close();

                                    } catch (SQLException e) {
                                        System.out.println(e);
                                    }


                                } else {

                                    //insert to friends

                                    PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends");
                                    ResultSet resultSet = preparedStatement1.executeQuery();

                                    boolean inserter = true;
                                    while (resultSet.next()) {
                                        if (!resultSet.getString("ip").equals((String.valueOf(inetAddress)).substring(1)) || !resultSet.getString("name").equals(message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))))) {
                                            inserter = true;
                                        } else {
                                            inserter = false;
                                            break;
                                        }
                                    }

                                    resultSet.close();
                                    preparedStatement1.close();

                                    if (inserter) {
                                        System.out.println((String.valueOf(inetAddress)).substring(1) + " " + message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))) + " " + Context.getInstance().getUser());
                                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("INSERT INTO friends(ip,name,username) values(?,?,?)");
                                        preparedStatement.setString(1, (String.valueOf(inetAddress)).substring(1));
                                        preparedStatement.setString(2, message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))));
                                        preparedStatement.setString(3, Context.getInstance().getUser());
                                        preparedStatement.executeUpdate();

                                        preparedStatement.close();
                                    }


                                    // add message to message DB

                                    try {
                                        PreparedStatement preparedStatement3 = Context.getInstance().getConnection().prepareStatement("SELECT f_id from friends where ip=? AND name=? AND username=?");
                                        preparedStatement3.setString(1, (String.valueOf(inetAddress)).substring(1));
                                        preparedStatement3.setString(2, message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))));
                                        preparedStatement3.setString(3, Context.getInstance().getUser());

                                        ResultSet resultSet3 = preparedStatement3.executeQuery();
                                        int f_id = 0;
                                        while (resultSet3.next()) {
                                            f_id = resultSet3.getInt("f_id");
                                        }

                                        resultSet3.close();
                                        preparedStatement3.close();

                                        PreparedStatement preparedStatement4 = Context.getInstance().getConnection().prepareStatement("INSERT INTO messages(message,is_mine,f_id,username) values(?,?,?,?)");
                                        preparedStatement4.setString(1, message.substring(0, message.indexOf("#-^.$")));
                                        preparedStatement4.setBoolean(2, false);
                                        preparedStatement4.setInt(3, f_id);
                                        preparedStatement4.setString(4, Context.getInstance().getUser());
                                        preparedStatement4.executeUpdate();
                                        preparedStatement4.close();

                                    } catch (Exception e) {
                                        System.out.println(e.getMessage() + " Error while inserting Message to database !");
                                    }


                                    Label label = new Label(message.substring((message.indexOf("#-^.$") + 5), (message.indexOf("~|}{`"))));
                                    label.getStylesheets().add("css/addListLabel.css");
                                    label.setId((String.valueOf(inetAddress)).substring(1));
                                    HBox hBox = new HBox();
                                    hBox.getChildren().add(label);
                                    hBox.setAlignment(Pos.CENTER);
                                    hBox.setStyle("-fx-background-color: rgba(0,168,355,0)");
                                    vBox.getChildren().add(hBox);
                                    vBox.setSpacing(20);


                                    label.setOnMouseClicked(event -> {
                                        Context.getInstance().setIP(label.getId());
                                        Context.getInstance().setUsername(label.getText());
                                        ds.close();

                                        try {
                                            Node source = (Node) event.getSource();
                                            Stage stage = (Stage) source.getScene().getWindow();
                                            Parent root = FXMLLoader.load(getClass().getResource("chatting.fxml"));
                                            stage.setTitle("MESSAGEme");
                                            stage.setScene(new Scene(root, 1000, 548));
                                        } catch (Exception exception) {
                                            System.out.println(exception.getClass());
                                        }

                                    });
                                }
                                linkedHashSet.add(String.valueOf(inetAddress));
                            } catch (Exception e) {
                                System.out.println(e.getMessage() + " new");
                            }

                      //  } //end if
                    });
                }

            } catch (Exception e) {
                System.out.println("Error on Client read!");
            }
        }
    }
}
