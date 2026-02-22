package tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestEvents {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String base = "http://localhost:8080";
        
        // Get token using debug endpoint
        System.out.println("--- Getting Token ---");
        HttpResponse<String> loginResp = client.send(
            HttpRequest.newBuilder()
                .uri(URI.create(base + "/api/debug/loginAsTestOrg"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );
        
        String token = null;
        String response = loginResp.body();
        if (response.contains("token")) {
            int start = response.indexOf("\"token\":\"") + 9;
            int end = response.indexOf("\"", start);
            token = response.substring(start, end);
            System.out.println("Token obtained: " + token.substring(0, 20) + "...");
        }
        
        if (token != null) {
            // Create a test event
            System.out.println("\n--- Creating Test Event ---");
            String eventJson = "{\"title\":\"Blood Donation Camp\",\"eventDate\":\"2025-10-15\",\"location\":\"City Hospital\"}";
            HttpResponse<String> createResp = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/org/1/events"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(eventJson))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Create Event Status: " + createResp.statusCode());
            System.out.println("Create Event Response: " + createResp.body());
            
            // List events
            System.out.println("\n--- Listing Events ---");
            HttpResponse<String> listResp = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/org/1/events"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("List Events Status: " + listResp.statusCode());
            System.out.println("List Events Response: " + listResp.body());
            
            // Test user events endpoint
            System.out.println("\n--- Testing User Events Endpoint ---");
            HttpResponse<String> userEventsResp = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/user/events"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("User Events Status: " + userEventsResp.statusCode());
            System.out.println("User Events Response: " + userEventsResp.body());
        }
    }
}