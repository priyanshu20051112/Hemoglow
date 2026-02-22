package tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SimpleEventTester {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String base = "http://localhost:8080";
        
        // Test public endpoints first
        System.out.println("--- Testing Public Endpoints ---");
        testGet(client, base + "/api/inventory/public?bloodGroup=O-", null);
        
        // Get token using debug endpoint
        System.out.println("\n--- Getting Token via Debug ---");
        testGet(client, base + "/api/debug/loginAsTestOrg", null);
        
        System.out.println("\n--- Testing Events Endpoints (without auth) ---");
        testGet(client, base + "/api/user/events", null);
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