package org.longbois.dashboard.components;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.longbois.dashboard.ApiService;
import org.longbois.dashboard.components.PanelController;

import java.io.IOException;

public class SidebarController {

    @FXML
    private Circle statusCircle;

    @FXML
    private Label statusText;

    @FXML
    private HBox sidebarDashboard;

    @FXML
    private HBox sidebarDataTable;

    @FXML
    private HBox sidebarSettings;

    @FXML
    private HBox sidebarWeather;

    @FXML
    private ComboBox<String> sidebarCombo;

    private final ApiService apiService;
    private final PanelController panelController;

    public SidebarController() {
        this.apiService = new ApiService();
        this.panelController = loadPanelController();
    }

    public void initialize() {
        startHealthCheck();

        // Init first station
        System.out.println("API - Fetching first station");
        System.out.println(panelController);
        JSONObject stationData = apiService.fetchData("http://bweerd.gcmsi.nl/api/stations");
        if (stationData == null) {
            System.out.println("API - Error while fetching data");
            return;
        }

        if (stationData.has("error")) {
            System.out.println("API - Error while fetching data: " + stationData.getString("error"));
            System.out.println();
            return;
        }

        JSONArray stations = stationData.getJSONArray("data");
        JSONObject firstStation = stations.getJSONObject(0);

        setCombobox(stations, firstStation);
    }

    private void startHealthCheck() {
        checkHealth();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> checkHealth()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void checkHealth() {
        System.out.println("API - Checking health");
        JSONObject healthData = apiService.fetchData("http://bweerd.gcmsi.nl/api/health");
        System.out.println(healthData);

        if (healthData == null) {
            statusCircle.getStyleClass().add("status-circle--offline");
            statusText.setText("Offline");
            return;
        }

        String databaseStatus = healthData.getJSONObject("database").getString("status");
        String apiStatus = healthData.getJSONObject("api").getString("status");

        if (databaseStatus.equals("OK") && apiStatus.equals("OK")) {
            float dbPing = healthData.getJSONObject("database").getFloat("ping");
            statusCircle.getStyleClass().add("status-circle--online");
            statusText.setText("Online (" + dbPing + "ms)");
        } else {
            statusCircle.getStyleClass().add("status-circle--offline");
            statusText.setText("Offline");
        }
    }

    private void setCombobox(JSONArray stations, JSONObject firstStation) {
        // Set prompt text
        sidebarCombo.setPromptText(firstStation.getString("name"));

        // Set combo box items
        for (int i = 0; i < stations.length(); i++) {
            JSONObject station = stations.getJSONObject(i);
            sidebarCombo.getItems().add(station.getString("name"));
        }

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

}
