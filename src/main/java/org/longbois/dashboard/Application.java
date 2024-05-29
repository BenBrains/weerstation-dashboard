package org.longbois.dashboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/org/longbois/dashboard/login/Login.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/org/longbois/dashboard/home/Home.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1380, 810);
        Image iconStream = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/lb-logo.png")));
        stage.getIcons().add(iconStream);
        stage.setTitle("LongBois™️");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}