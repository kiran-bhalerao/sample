import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
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

public class Home implements Initializable {

    @FXML
    private AnchorPane JoinPan;

    @FXML
    private AnchorPane GroupPan;

    @FXML
    private Label ipLabel;

    @FXML
    private VBox vBox;

    @FXML
    private ScrollPane scrollPane;

    RingProgressIndicator indicator;

    Stage stage;

    boolean checker = true;

    boolean isInNet = true;

    String ipString = "";


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
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage.setTitle("CHATme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    @FXML
    void profileClicked(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("UserProfile.fxml"));
        stage.setTitle("PROFILEme");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    void groupClicked(ActionEvent event) {
    }

    public void setGroupPan() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("GroupSelectPort.fxml"));
            stage.setTitle("GROUPSELECTme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    @FXML
    void joinClicked(ActionEvent event) {
    }

    public void setJoinPan() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("join.fxml"));
            stage.setTitle("JOINme");
            stage.setScene(new Scene(root, 1000, 548));
        } catch (Exception exception) {
            System.out.println(exception.getClass());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        RingProgressIndicator indicator = new RingProgressIndicator();
        RingProgressIndicator indicator1 = new RingProgressIndicator();
        clickedWorker(indicator, false);
        clickedWorker(indicator1, true);
        JoinPan.getChildren().addAll(indicator);
        GroupPan.getChildren().addAll(indicator1);

        getSetIP();

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // get chatting history from friends table and call addHistoryList();

        try {
            PreparedStatement preparedStatement1 = Context.getInstance().getConnection().prepareStatement("SELECT ip,name FROM friends WHERE username=?");
            preparedStatement1.setString(1, Context.getInstance().getUser());
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                addHistoryList(resultSet.getString("name"), resultSet.getString("ip"));
            }
            resultSet.close();
            preparedStatement1.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void getSetIP() {
        Enumeration<NetworkInterface> nets;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                String res = displayInterfaceInformation(netint);
                if (!res.equals("")) {
                    ipString = res;
                }
            }
        } catch (SocketException e) {
            System.out.println("get Network Interfaces (IP) exception");
        }

        if (!ipString.equals("")) {
            ipLabel.setText(ipString);
            Context.getInstance().setMyIp(ipString);
        } else {
            isInNet = false;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                Platform.runLater(() -> {
                    try {
                        noNetDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }).start();
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

    public void noNetDialog() throws Exception {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent root;
        root = FXMLLoader.load(getClass().getResource("noNetDialog.fxml"));
        Scene scene = new Scene(root, 410, 255);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
    }


    public void addHistoryList(String text, String id) {
        Label label = new Label(text);
        label.getStylesheets().add("css/addListLabel.css");
        label.setId(String.valueOf(id));
        HBox hBox = new HBox();
        hBox.getChildren().add(label);
        hBox.setAlignment(Pos.CENTER);
        hBox.setStyle("-fx-background-color: rgba(0,168,355,0)");
        vBox.getChildren().add(hBox);
        vBox.setSpacing(20);

        label.setOnContextMenuRequested(event -> {
            //isContextMenuClicked = false;

            Context.getInstance().setFromStageTODelete("home");
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

        label.setOnMouseClicked(event -> {
            // set Offline or Online Mode ...
            Context.getInstance().setHomeStage((Stage) ((Node) event.getSource()).getScene().getWindow());
            Context.getInstance().setIP(id);
            Context.getInstance().setUsername(text);

            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                Parent root;
                root = FXMLLoader.load(getClass().getResource("OnlineOrOfflineDialog.fxml"));
                Scene scene = new Scene(root, 410, 255);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void clickedWorker(RingProgressIndicator indicator, boolean isGroup) {
        indicator.setOnMouseClicked(event -> {
            Node source = (Node) event.getSource();
            stage = (Stage) source.getScene().getWindow();
            if (checker) {
                new WorkerThread(indicator, isGroup).start();
                checker = false;
            }
        });
    }


    public class WorkerThread extends Thread {

        RingProgressIndicator ringProgressIndicator;
        int count;
        boolean isGroup;

        public WorkerThread(RingProgressIndicator ringProgressIndicator, boolean isGroup) {
            this.ringProgressIndicator = ringProgressIndicator;
            this.isGroup = isGroup;
        }

        @Override
        public void run() {
            if (isInNet) {
                while (true) {
                    if (count < 20) {
                        try {
                            Thread.sleep(60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (count < 50) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (count < 70) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Platform.runLater(() -> {
                        ringProgressIndicator.setProgress(count);
                    });
                    count++;
                    if (count > 100) {
                        Platform.runLater(() -> {

                            if (isGroup)
                                setGroupPan();
                            if (!isGroup)
                                setJoinPan();
                        });
                        break;
                    }
                }
            }
        }
    }


}
