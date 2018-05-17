import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

public class Context {


    private final static Context instance = new Context();
    private Signup signup;
    private Join join;
    private String ip;
    private String port;
    private String username;
    private String user;
    private Stage joinStage;
    private String myIp;
    private Stage homeStage;
    private String deleteLabelName = null;
    private String password;

    private String fromStageTODelete;


    private Label label;
    private int labelId;

    //private String path=System.getenv("APPDATA");
    private String path = System.getProperty("user.home");
    public Connection connection;


    public static Context getInstance() {
        return instance;
    }

    public Context() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+path+"/DatabaseDemo.sqlite");
        } catch (Exception e) {
            System.out.println(e.getMessage() + " connection class problem !");
            connection = null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setSignup(Signup signup) {
        this.signup = signup;
    }

    public Signup getSignup() {
        return signup;
    }

    public void setJoin(Join join) {
        this.join = join;
    }

    public Join getJoin() {
        return join;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIP() {
        return ip;
    }

    public String getPORT() {
        return port;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setPORT(String port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public Stage getJoinStage() {
        return joinStage;
    }

    public void setJoinStage(Stage joinStage) {
        this.joinStage = joinStage;
    }

    public String getMyIp() {
        return myIp;
    }

    public void setMyIp(String myIp) {
        this.myIp = myIp;
    }

    public Stage getHomeStage() {
        return homeStage;
    }

    public void setHomeStage(Stage homeStage) {
        this.homeStage = homeStage;
    }

    public String getDeleteLabelName() {
        return deleteLabelName;
    }

    public void setDeleteLabelName(String deleteLabelName) {
        this.deleteLabelName = deleteLabelName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFromStageTODelete() {
        return fromStageTODelete;
    }

    public void setFromStageTODelete(String fromStageTODelete) {
        this.fromStageTODelete = fromStageTODelete;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }


}