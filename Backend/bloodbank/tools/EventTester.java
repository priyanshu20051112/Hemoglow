package tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class EventTester {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String base = "http://localhost:8080";
        
        // Get token first
        String loginJson = "{\"username\":\"testorg\",\"password\":\"test123\"}";
        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(base + "/api/org/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build();
        
        HttpResponse<String> loginResp = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("Login status: " + loginResp.statusCode());
        System.out.println("Login response: " + loginResp.body());
        
        // Extract token (simplified)
        String token = null;
        if (loginResp.body().contains("token")) {
            int start = loginResp.body().indexOf("\"token\":\"") + 9;
            int end = loginResp.body().indexOf("\"", start);
            token = loginResp.body().substring(start, end);
            System.out.println("Token: " + token);
        }
        
        if (token != null) {
            // Test events endpoint
            System.out.println("\n--- Testing Events Endpoints ---");
            testGet(client, base + "/api/user/events", token);
            
            // Test inventory endpoint 
            System.out.println("\n--- Testing Inventory Endpoints ---");
            testGet(client, base + "/api/inventory/public?bloodGroup=O-", null);
        }
    }
    
    static void testGet(HttpClient client, String url, String token) {
        try {
            System.out.println("GET " + url);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10));
            
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }
            
            HttpRequest req = builder.build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + resp.statusCode());
            System.out.println("Response: " + resp.body());
            System.out.println("----");
        } catch (Exception e) {
            System.out.println("GET failed: " + e.getMessage());
        }
    }
}