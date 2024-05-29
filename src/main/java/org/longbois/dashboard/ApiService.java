package org.longbois.dashboard;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ApiService {

    private static String apiKey;

    public ApiService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);

            apiKey = prop.getProperty("API_KEY");

        } catch (IOException ex) {
            System.out.println("Init - An error occurred while loading the properties file.");
            ex.printStackTrace();
        }
    }

    public static JSONObject fetchData(String apiUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).header("API_KEY", apiKey).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
