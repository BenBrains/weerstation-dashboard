package org.longbois.dashboard.components;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.longbois.dashboard.factories.ControllerFactory;
import org.longbois.dashboard.services.ApiService;
import org.longbois.dashboard.services.HealthCheckService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SidebarController {

    @FXML
    private Circle statusCircle;

    @FXML
    private Label statusText;

    @FXML
    private HBox sidebarDashboard;

    @FXML
    private HBox sidebarSensors;

    @FXML
    private HBox sidebarSettings;

    @FXML
    private HBox sidebarWeather;

    @FXML
    private ComboBox<String> sidebarCombo;

    private Map<String, Scene> sceneCache = new HashMap<>();

    private final ApiService apiService;
    private final PanelController panelController;

    public SidebarController() {
        this.apiService = new ApiService();
        this.panelController = loadPanelController();
    }

    public void initialize() {
        ControllerFactory.addController(SidebarController.class, this);

        HealthCheckService.getInstance(statusCircle, statusText).startHealthCheck();

        // Navigation
        setNavigation(sidebarDashboard, "/org/longbois/dashboard/home/Home.fxml");
        setNavigation(sidebarSensors, "/org/longbois/dashboard/sensors/Sensors.fxml");
        setNavigation(sidebarSettings, "/org/longbois/dashboard/settings/Settings.fxml");
        setNavigation(sidebarWeather, "/org/longbois/dashboard/weather/Weather.fxml");

        // Init first station
        System.out.println("API - Fetching first station");
        JSONObject stationData = apiService.fetchData("http://bweerd.gcmsi.nl/api/stations");
        if (stationData == null) {
            System.out.println("API - Error while fetching data");
            return;
        }

        if (stationData.has("error")) {
            System.out.println("API - Error while fetching data: " + stationData.getString("error"));
            return;
        }

        JSONArray stations = stationData.getJSONArray("data");
        JSONObject firstStation = stations.getJSONObject(0);

        setCombobox(stations, firstStation);
    }

    private void setCombobox(JSONArray stations, JSONObject firstStation) {
        // Set combo box items
        for (int i = 0; i < stations.length(); i++) {
            JSONObject station = stations.getJSONObject(i);
            sidebarCombo.getItems().add(station.getString("name"));
        }

        sidebarCombo.getSelectionModel().selectFirst();

        // Listen for combo box changes
        sidebarCombo.setOnAction(event -> {
            String selectedStation = sidebarCombo.getSelectionModel().getSelectedItem();
            System.out.println("Selected station: " + selectedStation);
            for (int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);
                if (station.getString("name").equals(selectedStation)) {
                    panelController.setPanelInfo(station);
                }
            }
        });
    }

    private PanelController loadPanelController() {
        try {
            System.out.println("Attempting to load PanelController...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/longbois/dashboard/components/Panel.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load PanelController", e);
        }
    }

   private void setNavigation(HBox hbox, String fxmlPath) {
        hbox.setOnMouseClicked(event -> changeScene(fxmlPath));
   }

   private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) sidebarDashboard.getScene().getWindow();
            Scene newScene = sceneCache.get(fxmlPath);

            if (newScene == null) {
                System.out.println("Loading new scene: " + fxmlPath);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent newParent = loader.load();
                newScene = new Scene(newParent);
            } else {
                System.out.println("Loading cached scene: " + fxmlPath);
            }
            stage.setScene(newScene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
   }

    // Stuff for export, this whole codebase is a frikkin mess. Or I am just too dumb to understand it.
    public String getSelectedStation() {
        if (sidebarCombo.getSelectionModel().isEmpty()) {
            return null;
        }

        return sidebarCombo.getSelectionModel().getSelectedItem();
    }
}
