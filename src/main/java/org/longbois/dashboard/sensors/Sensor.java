package org.longbois.dashboard.sensors;

public class Sensor {
    private String sensorDepth;
    private String sensorId;
    private String sensorName;
    private String sensorType;
    private String sensorUnit;
    private String stationId;

    // Getters
    public String getSensorDepth() {
        return sensorDepth;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getSensorUnit() {
        return sensorUnit;
    }

    public String getStationId() {
        return stationId;
    }

    // Setters
    public void setSensorDepth(String sensorDepth) {
        this.sensorDepth = sensorDepth;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public void setSensorUnit(String sensorUnit) {
        this.sensorUnit = sensorUnit;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}