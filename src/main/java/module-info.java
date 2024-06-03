module org.longbois.dashboard {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.net.http;
    requires org.json;
    requires jdk.jfr;

    opens org.longbois.dashboard.login to javafx.fxml;
    opens org.longbois.dashboard.home to javafx.fxml;
    exports org.longbois.dashboard;
}