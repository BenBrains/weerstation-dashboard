package org.longbois.dashboard.sensors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.longbois.dashboard.services.ApiService;

import java.io.IOException;

public class SensorsController {

    @FXML
    private TableColumn<Sensor, String> sensorDepth, sensorId, sensorName, sensorType, sensorUnit, stationId;

    @FXML
    private TableView<Sensor> sensorsTable;

    private final ApiService apiService;

    public SensorsController() {
        this.apiService = new ApiService();
    }

    public void initialize() {
        try {
            System.out.println("API - Fetching all sensors");
            JSONObject sensorsData = apiService.fetchData("http://bweerd.gcmsi.nl/api/sensors");
            if (sensorsData == null) {
                System.out.println("API - Error while fetching sensors data");
                return;
            }

            if (sensorsData.has("error")) {
                System.out.println("API - Error while fetching data: " + sensorsData.getString("error"));
                System.out.println();
                return;
            }

            JSONArray sensors = sensorsData.getJSONArray("data");
            ObservableList<Sensor> sensorList = FXCollections.observableArrayList();

            for (int i = 0; i < sensors.length(); i++) {
                JSONObject sensor = sensors.getJSONObject(i);
                Sensor s = new Sensor();

                String depth = sensor.isNull("depth") ? null : sensor.getString("depth");
                s.setSensorDepth(depth);
                s.setSensorId(String.valueOf(sensor.getInt("id")));
                s.setSensorName(sensor.getString("name"));
                s.setSensorType(sensor.getString("type"));
                s.setSensorUnit(sensor.getString("unit"));
                s.setStationId(String.valueOf(sensor.getInt("station_id")));

                sensorList.add(s);
            }

            sensorDepth.setCellValueFactory(new PropertyValueFactory<>("sensorDepth"));
            sensorId.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
            sensorName.setCellValueFactory(new PropertyValueFactory<>("sensorName"));
            sensorType.setCellValueFactory(new PropertyValueFactory<>("sensorType"));
            sensorUnit.setCellValueFactory(new PropertyValueFactory<>("sensorUnit"));
            stationId.setCellValueFactory(new PropertyValueFactory<>("stationId"));

            sensorsTable.setItems(sensorList);
            sensorsTable.setRowFactory(tv -> {
                TableRow<Sensor> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!row.isEmpty()) {
                        event.consume();
                    }
                });
                return row;
            });

        } catch (Exception e) {
            System.out.println("API - Error while fetching data");
            e.printStackTrace();
        }


    }

    @FXML
    void createNewSensor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/longbois/dashboard/newSensor/newSensor.fxml"));
            Parent newSensorParent = loader.load();
            Scene newSensorScene = new Scene(newSensorParent);
            Stage currentStage = (Stage) sensorsTable.getScene().getWindow();
            currentStage.setScene(newSensorScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
