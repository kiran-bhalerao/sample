import javafx.animation.AnimationTimer;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.*;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class Chatting implements Initializable {


    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ScrollPane scrollPane2;

    @FXML
    private VBox vBox2;

    @FXML
    public TextField textField;

    @FXML
    private VBox vBox;

    @FXML
    private Label myIp;

    @FXML
    private Label friendsNameLabel;

    @FXML
    private Text ic_txt;

    @FXML
    private Label user;



    boolean isSender = true;

    private int count;

    int id;

    boolean isServer = true;

    Node source = null;

    private String ip;
    private int port;
    String username;

    private InetAddress inetAddress;
    private DatagramSocket ds;


    private LinkedHashSet<String> linkedHashSet;
    private Iterator<String> iterator;


    @FXML
    void close(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void enterKey(KeyEvent event) {

        source = (Node) event.getSource();

        if (event.getCode() == KeyCode.getKeyCode("Enter")) {
            new Thread(() -> {
                try {
                    byte[] data = (textField.getText() + "#-^.$" + Context.getInstance().getUser()+"~|}{`"+Context.getInstance().getUsername()).getBytes();
                    DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, port);
                    ds.send(dp);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + " Error while pressed enter key !");
                }
            }).start();

            addMessageToDB((textField.getText() + "#-^.$" + Context.getInstance().getUser()), inetAddress, username, true);
            setMessage(textField.getText(), true, -1);
            textField.setText("");
        }
    }

    @FXML
    void backClicked(MouseEvent event) {
        try {
            ds.close();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            stage.setTitle("MESSAGEme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }


    public synchronized void addMessageToDB(String message, InetAddress inetAddress, String username, boolean isMine) {
        try {
            System.out.println(String.valueOf(inetAddress).substring(1) + " " + username + " " + Context.getInstance().getUser());
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT f_id from friends where ip=? AND name=? AND username=?");
            preparedStatement.setString(1, String.valueOf(inetAddress).substring(1));
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, Context.getInstance().getUser());

            ResultSet resultSet = preparedStatement.executeQuery();
            int f_id = 0;
            while (resultSet.next()) {
                f_id = resultSet.getInt("f_id");
            }

            System.out.println(f_id);
            resultSet.close();
            preparedStatement.close();

            PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("INSERT INTO messages(message,is_mine,f_id,username) values(?,?,?,?)");
            preparedStatement1.setString(1, message.substring(0, message.indexOf("#-^.$")));
            preparedStatement1.setBoolean(2, isMine);
            preparedStatement1.setInt(3, f_id);
            preparedStatement1.setString(4, Context.getInstance().getUser());
            preparedStatement1.executeUpdate();
            preparedStatement1.close();

        } catch (Exception e) {
            System.out.println(e.getMessage() + " Error while inserting Message to database !");
        }
    }

    @FXML
    void sendClicked(ActionEvent event) {

        source = (Node) event.getSource();

        new Thread(() -> {
            try {
                byte[] data = (textField.getText() + "#-^.$" + Context.getInstance().getUser()+"~|}{`"+Context.getInstance().getUsername()).getBytes();
                DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, port);
                ds.send(dp);
            } catch (Exception e) {
                System.out.println(e.getClass() + " in sending Thread of sendClicked()");
            }
        }).start();

        addMessageToDB((textField.getText() + "#-^.$" + Context.getInstance().getUser()), inetAddress, username, true);
        setMessage(textField.getText(), true, -1);
        textField.setText("");
    }


    public synchronized void setMessage(String msg, boolean isSender, int index) {
        try {
            if (!msg.equals("")) {
                if (isSender) {

                    if (index == -1) {
                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT m_id FROM messages");
                        ResultSet rs = preparedStatement.executeQuery();
                        index = 1;
                        while (rs.next()) {
                            index = rs.getInt(1);
                        }
                        rs.close();
                        preparedStatement.close();
                    }


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

                    if (index == -1) {

                        PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT m_id FROM messages");
                        ResultSet rst = preparedStatement.executeQuery();
                        index = 1;
                        while (rst.next()) {
                            index = rst.getInt(1);
                        }
                        rst.close();
                        preparedStatement.close();
                    }

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
                if (source != null)
                    scrollNodeInTopScrollPane(source, scrollPane);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " Error in adding message to labels !");
        }

        id++;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ip = Context.getInstance().getIP();
            port = Integer.parseInt(Context.getInstance().getPORT());
            username = Context.getInstance().getUsername();
        } catch (Exception e) {
            System.out.println(e.getMessage() + "error in initializing ip and port");
        }

        linkedHashSet = new LinkedHashSet<>();

        myIp.setText(Context.getInstance().getMyIp() + " : " + Context.getInstance().getPORT());
        friendsNameLabel.setText(Context.getInstance().getUsername());

        ic_txt.setText(friendsNameLabel.getText().substring(0, 1).toUpperCase());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        try {
            inetAddress = InetAddress.getByName(ip);
            ds = new DatagramSocket(port);
        } catch (Exception e) {
            System.out.println("inetAddress error " + e.getMessage());
        }


        try {
            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("SELECT m_id, message , is_mine FROM messages WHERE f_id in(SELECT f_id FROM friends WHERE ip=? AND name=?) AND username=?");
            preparedStatement.setString(1, String.valueOf(inetAddress).substring(1));
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, Context.getInstance().getUser());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("print");
                setMessage(resultSet.getString("message"), resultSet.getBoolean("is_mine"), resultSet.getInt("m_id"));
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage() + " error while setting old messages !");
        }

        user.setText(Context.getInstance().getUser());

        try {
            byte[] data = String.valueOf("#-^.$" + Context.getInstance().getUser()+"~|}{`"+Context.getInstance().getUsername()).getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, port);
            ds.send(dp);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " error while sending first message !");
        }

        Thread thread = new Thread(new ReceiverThread());
        thread.start();

    }

    public void scrollNodeInTopScrollPane(Node n, ScrollPane s) {
        final Node node = n;
        final ScrollPane clientTopScrollPane = s;
        AnimationTimer timer = new AnimationTimer() {
            long lng = 0;

            @Override
            public void handle(long l) {
                if (lng == 0) {
                    lng = l;
                }
                if (l > lng + 100000000) {
                    if (node.getLocalToSceneTransform().getTy() > 20) {
                        clientTopScrollPane.setVvalue(clientTopScrollPane.getVvalue() + 0.05);
                        if (clientTopScrollPane.getVvalue() == 1) {
                            this.stop();
                        }
                    } else {
                        this.stop();
                    }
                }
            }
        };
        timer.start();
    }


    public class ReceiverThread extends Thread {
        public ReceiverThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg;
                    byte[] receivePKT = new byte[1024];
                    DatagramPacket dp1 = new DatagramPacket(receivePKT, receivePKT.length);
                    ds.receive(dp1);
                    InetAddress receivingInetAddress = dp1.getAddress();
                    msg = new String(dp1.getData());

                    Platform.runLater(() -> {

                        if (msg.substring((msg.indexOf("~|}{`") + 5), dp1.getLength()).equals(Context.getInstance().getUser())) {

                            if ((String.valueOf(receivingInetAddress).substring(1)).equals(ip) && msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))).equals(Context.getInstance().getUsername())) {
                                addMessageToDB(msg, receivingInetAddress, msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))), false);
                                setMessage(msg.substring(0, msg.indexOf("#-^.$")), false, -1);

                            } else {

                                boolean isInList = false;
                                try {
                                    iterator = linkedHashSet.iterator();
                                    while (iterator.hasNext()) {
                                        isInList = String.valueOf(receivingInetAddress).equals(iterator.next());
                                        if (isInList)
                                            break;
                                    }
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                                System.out.println(isInList);
                                if (isInList) {
                                    //Add others msg to database....
                                    addMessageToDB(msg, receivingInetAddress, msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))), false);

                                } else {
                                    //Add others msg to database....
                                    addMessageToDB(msg, receivingInetAddress, msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))), false);

                                    try {
                                        //add friend to Friends database....
                                        PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends");
                                        ResultSet resultSet = preparedStatement1.executeQuery();
                                        boolean inserter = true;
                                        while (resultSet.next()) {
                                            if (!resultSet.getString("ip").equals((String.valueOf(receivingInetAddress)).substring(1)) || !resultSet.getString("name").equals(msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))))) {
                                                inserter = true;
                                            } else {
                                                inserter = false;
                                                break;
                                            }
                                        }
                                        resultSet.close();
                                        preparedStatement1.close();

                                        if (inserter) {
                                            System.out.println((String.valueOf(receivingInetAddress)).substring(1) + " " + msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))) + " " + Context.getInstance().getUser());
                                            PreparedStatement preparedStatement = Context.getInstance().getConnection().prepareStatement("INSERT INTO friends(ip,name,username) values(?,?,?)");
                                            preparedStatement.setString(1, (String.valueOf(receivingInetAddress)).substring(1));
                                            preparedStatement.setString(2, msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))));
                                            preparedStatement.setString(3, Context.getInstance().getUser());
                                            preparedStatement.executeUpdate();
                                            preparedStatement.close();
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }


                                    Label label = new Label(msg.substring((msg.indexOf("#-^.$") + 5), (msg.indexOf("~|}{`"))));
                                    label.getStylesheets().add("css/addListLabel.css");
                                    label.setId((String.valueOf(receivingInetAddress)).substring(1));
                                    HBox hBox = new HBox();
                                    hBox.getChildren().add(label);
                                    hBox.setAlignment(Pos.CENTER);
                                    hBox.setStyle("-fx-background-color: rgba(0,168,355,0)");
                                    vBox2.getChildren().add(hBox);
                                    vBox2.setSpacing(20);

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

                                linkedHashSet.add(String.valueOf(receivingInetAddress));
                                System.out.println(linkedHashSet);
                            }

                        }  else {
                            System.out.println(msg);
                        }// first if ends..
                    });
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}




