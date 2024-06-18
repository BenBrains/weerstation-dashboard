package org.longbois.dashboard.newSensor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import org.longbois.dashboard.components.SidebarController;
import org.longbois.dashboard.factories.ControllerFactory;
import org.longbois.dashboard.services.ApiService;

public class NewSensorController {

    @FXML
    private TextField depth;

    @FXML
    private TextField name;

    @FXML
    private MenuButton type;

    @FXML
    private MenuButton unit;

    @FXML
    private Label errorMessage;

    private final ApiService apiService;

    SidebarController sidebarController;

    public NewSensorController() {
        this.apiService = new ApiService();
    }

    public void initialize() {

        sidebarController = ControllerFactory.getController(SidebarController.class);

        for (MenuItem menuItem : type.getItems()) {
            menuItem.setOnAction(event -> type.setText(menuItem.getText()));
        }

        for (MenuItem menuItem : unit.getItems()) {
            menuItem.setOnAction(event -> unit.setText(menuItem.getText()));
        }
    }

    @FXML
    void saveSensor(ActionEvent event) {
        String sensorName = name.getText();
        String sensorDepth = depth.getText();
        String sensorType = type.getText();
        String sensorUnit = unit.getText();

        // Validation
        if (sensorName.isEmpty() || sensorDepth.isEmpty() || sensorType.isEmpty() || sensorUnit.isEmpty()) {
            System.out.println("Please fill in all fields");
        }

        if (!sensorDepth.matches("-?\\d+(\\.\\d+)?")) {
            System.out.println("Depth must be a number");
        }

        System.out.println("Form info: " + sensorName + " " + sensorDepth + " " + sensorType + " " + sensorUnit);

        String selectedStation = sidebarController.getSelectedStation();

        System.out.println("Selected station: " + selectedStation);

//        Create a JSON object with the sensor data and post it to the API
        JSONObject sensorData = new JSONObject();
        sensorData.put("name", sensorName);
        sensorData.put("depth", sensorDepth);
        sensorData.put("type", sensorType.toLowerCase());
        sensorData.put("unit", sensorUnit);
        sensorData.put("stationId", 1);

        System.out.println("JSONObject" + sensorData);
//        String jsonObject = "{\"name\":\"" + sensorName + "\",\"depth\":" + sensorDepth + ",\"type\":\"" + sensorType.toLowerCase() + "\",\"unit\":\"" + sensorUnit + "\",\"station_id\":1}";
        JSONObject response = apiService.postData("http://bweerd.gcmsi.nl/api/sensors", jsonObject);
        if (response == null) {
            System.out.println("API - Error while posting data");
            return;
        }

        if (response.has("error")) {
            System.out.printf("API - %s while posting data: %s%n", response.getString("error"), response.getString("message"));
            errorMessage.setText(response.getString("message"));
            errorMessage.setVisible(true);
            return;
        }

        System.out.println("API - Sensor created: " + response.getJSONObject("data").getString("name"));
    }

}
