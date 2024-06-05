package org.longbois.dashboard.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.json.JSONObject;

public class PanelController {

    @FXML
    private Label panelName;

    @FXML
    private Label panelLocation;

    @FXML
    private Label panelLatLon;

    @FXML
    private Label panelSoftwareVersion;

    @FXML
    private Label panelHardwareVersion;

    public PanelController() {
    }

    public void initialize() {
    }

    public void setPanelInfo(JSONObject station) {
        try {
            System.out.printf("Setting panel info for station %s%n", station.getString("name"));
            double lat = station.optDouble("lat", Double.NaN);
            double lon = station.optDouble("lon", Double.NaN);
            String latDisplay = Double.isNaN(lat) ? "-" : String.valueOf(lat);
            String lonDisplay = Double.isNaN(lon) ? "-" : String.valueOf(lon);

//            Platform.runLater(() -> {
                panelName.setText(station.getString("name"));
                panelLocation.setText(station.getString("location"));
                panelLatLon.setText(latDisplay + ", " + lonDisplay);
                panelSoftwareVersion.setText(station.getString("software_version"));
                panelHardwareVersion.setText(station.getString("hardware_version"));

//                System.out.println("Panel Name: " + panelName.getText());
//                System.out.println("Panel Location: " + panelLocation.getText());
//                System.out.println("Panel LatLon: " + panelLatLon.getText());
//                System.out.println("Panel Software Version: " + panelSoftwareVersion.getText());
//                System.out.println("Panel Hardware Version: " + panelHardwareVersion.getText());

                panelName.getParent().requestLayout();

//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}