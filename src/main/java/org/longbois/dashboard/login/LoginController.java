package org.longbois.dashboard.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private PasswordField apiKeyInput;

    @FXML
    private Text invalidKey;

    @FXML
    void logIn(ActionEvent event) {
        if (apiKeyInput.getText().equals("1234")) {
            System.out.println("Info - Logged in");

            try {
                Stage stage = (Stage) apiKeyInput.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/longbois/dashboard/home/Home.fxml"));
                Parent homeParent = loader.load();
                Scene  homeScene = new Scene(homeParent, 1380, 780);
                stage.setScene(homeScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            invalidKey.setVisible(true);
            System.out.println("Error - Invalid API key!");
        }
    }

}
