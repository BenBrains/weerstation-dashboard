package org.longbois.dashboard.services;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.json.JSONObject;

public class HealthCheckService {
    private static HealthCheckService instance;
    private final ApiService apiService;
    private final Timeline timeline;
    private final Circle statusCircle;
    private final Label statusText;

    private HealthCheckService(Circle statusCircle, Label statusText) {
        this.apiService = new ApiService();
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> checkHealth()));
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.statusCircle = statusCircle;
        this.statusText = statusText;
    }

    public static HealthCheckService getInstance(Circle statusCircle, Label statusText) {
        if (instance == null) {
            instance = new HealthCheckService(statusCircle, statusText);
        }
        return instance;
    }

    public void startHealthCheck() {
        checkHealth();
        timeline.play();
    }

    private void checkHealth() {
        JSONObject healthData = apiService.fetchData("http://bweerd.gcmsi.nl/api/health");

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
