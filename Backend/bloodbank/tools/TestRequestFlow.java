package tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestRequestFlow {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String base = "http://localhost:8080";
        
        // Test the inventory endpoint first
        System.out.println("--- Testing Inventory Endpoint ---");
        HttpResponse<String> inventoryResp = client.send(
            HttpRequest.newBuilder()
                .uri(URI.create(base + "/api/inventory/public?bloodGroup=A%2B"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("Inventory Status: " + inventoryResp.statusCode());
        System.out.println("Inventory Response: " + inventoryResp.body());
        
        // Parse the response to get an organization ID
        String response = inventoryResp.body();
        if (response.contains("\"orgId\":")) {
            String[] parts = response.split("\"orgId\":");
            if (parts.length > 1) {
                String orgIdPart = parts[1].split(",")[0].trim();
                Long orgId = Long.parseLong(orgIdPart);
                System.out.println("Found organization ID: " + orgId);
                
                // Now test making a request
                System.out.println("\n--- Testing Blood Request ---");
                String requestJson = String.format(
                    "{\"patient\":\"Test Patient\",\"bloodGroup\":\"A+\",\"hospital\":\"Test Hospital\",\"phone\":\"1234567890\",\"amount\":2,\"organization\":{\"id\":%d}}",
                    orgId
                );
                System.out.println("Request payload: " + requestJson);
                
                HttpResponse<String> requestResp = client.send(
                    HttpRequest.newBuilder()
                        .uri(URI.create(base + "/api/user/1/requests"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
                System.out.println("Request Status: " + requestResp.statusCode());
                System.out.println("Request Response: " + requestResp.body());
            }
        }
    }
}