package org.longbois.dashboard.home;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.longbois.dashboard.ApiService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    private List<LineChart<String, Number>> charts = new ArrayList<>();


    @FXML
    private VBox graphContainer;

    @FXML
    private Label panelBattery;

    @FXML
    private Label panelHardwareVersion;

    @FXML
    private Label panelLatLon;

    @FXML
    private Label panelLocation;

    @FXML
    private Label panelName;

    @FXML
    private Label panelSoftwareVersion;

    @FXML
    private HBox sidebarDashboard;

    @FXML
    private HBox sidebarDataTable;

    @FXML
    private HBox sidebarSettings;

    @FXML
    private HBox sidebarWeather;

    @FXML
    private Circle statusCircle;

    @FXML
    private Label statusText;

    @FXML
    private ComboBox<String> sidebarCombo;

    private final ApiService apiService;

    public HomeController() {
        this.apiService = new ApiService();
    }

    public void initialize() {

        startHealthCheck();

        try {
            // Init first station
            System.out.println("API - Fetching first station");
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
            setPanelInfo(firstStation);
            addGraphsToContainer(firstStation.getInt("id"));
        } catch (Exception e) {
            System.out.println("API - Error while fetching data");
            e.printStackTrace();
        }
    }

    private LineChart<String, Number> createChart(String title, JSONArray datapoints) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefHeight(300);
        lineChart.setLegendVisible(false);

        xAxis.setTickLabelFont(new Font("Comic Sans MS", 12));
        yAxis.setTickLabelFont(new Font("Comic Sans MS", 12));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (int i = 0; i < datapoints.length(); i++) {
            JSONObject datapoint = datapoints.getJSONObject(i);
            String timestamp = datapoint.getString("timestamp");
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd - HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
            String formattedTimestamp = dateTime.format(outputFormatter);
            double value = datapoint.getDouble("value");
            series.getData().add(new XYChart.Data<>(formattedTimestamp, value));

        }
        lineChart.getData().add(series);
        return lineChart;
    }

    public void addGraphsToContainer(int stationId) {
        JSONObject response = apiService.fetchData("http://bweerd.gcmsi.nl/api/sensors");
        assert response != null;
        JSONArray sensors = response.getJSONArray("data");

        for (int i = 0; i < sensors.length(); i++) {
            JSONObject sensor = sensors.getJSONObject(i);
            if (sensor.getInt("station_id") == stationId) {
                JSONObject sensorResponse = apiService.fetchData("http://bweerd.gcmsi.nl/api/sensors/" + String.valueOf(sensor.getInt("id")) + "/between?start=2022-01-01&end=2024-12-31");
                assert sensorResponse != null;
                JSONObject sensorData = sensorResponse.getJSONObject("data");

                // Print the sensor data for debugging
                System.out.println("Sensor data: " + sensorData.toString());

                Platform.runLater(() -> {
                    LineChart<String, Number> chart = createChart(sensorData.getString("name"), sensorData.getJSONArray("datapoints"));
                    charts.add(chart);
                    graphContainer.getChildren().add(chart);

                });
            }
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
                    setPanelInfo(station);
                }
            }
        });
    }

    private void setPanelInfo(JSONObject station) {
        double lat = station.optDouble("lat", Double.NaN);
        double lon = station.optDouble("lon", Double.NaN);
        String latDisplay = Double.isNaN(lat) ? "-" : String.valueOf(lat);
        String lonDisplay = Double.isNaN(lon) ? "-" : String.valueOf(lon);

        panelName.setText(station.getString("name"));
        panelLocation.setText(station.getString("location"));
        panelLatLon.setText(latDisplay + ", " + lonDisplay);
        panelSoftwareVersion.setText(station.getString("software_version"));
        panelHardwareVersion.setText(station.getString("hardware_version"));
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

}