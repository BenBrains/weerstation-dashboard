package org.longbois.dashboard.home;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;
import org.longbois.dashboard.services.ApiService;
import org.longbois.dashboard.components.PanelController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    private List<LineChart<String, Number>> charts = new ArrayList<>();

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
    private VBox graphContainer;

    private final ApiService apiService;
    private final PanelController panelController;

    public HomeController() {
        this.apiService = new ApiService();
        this.panelController = loadPanelController();
    }

    public void initialize() {

        try {
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

            try {
                System.out.println("API - Setting panel info...");
                panelController.setPanelInfo(firstStation);
            } catch (Exception e) {
                System.out.println("API - Error while setting panel info");
                e.printStackTrace();
            }

            addGraphsToContainer(firstStation.getInt("id"));
        } catch (Exception e) {
            System.out.println("API - Error while fetching data");
            e.printStackTrace();
        }
    }

    private PanelController loadPanelController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/longbois/dashboard/components/Panel.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load PanelController", e);
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

                Platform.runLater(() -> {
                    LineChart<String, Number> chart = createChart(sensorData.getString("name"), sensorData.getJSONArray("datapoints"));
                    charts.add(chart);
                    graphContainer.getChildren().add(chart);
                });
            }
        }
    }
}