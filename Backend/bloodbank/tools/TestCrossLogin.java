import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestCrossLogin {
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        
        // Test 1: Login as user
        System.out.println("=== Testing User Login ===");
        testLogin(client, "/api/user/login", "annanay033", "123456");
        
        // Test 2: Login as organization 
        System.out.println("\n=== Testing Organization Login ===");
        testLogin(client, "/api/org/login", "annanay033", "123456");
        
        // Test 3: Try different credentials
        System.out.println("\n=== Testing with Different User ===");
        testLogin(client, "/api/user/login", "testuser", "password");
        testLogin(client, "/api/org/login", "testuser", "password");
    }
    
    private static void testLogin(HttpClient client, String endpoint, String username, String password) {
        try {
            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Endpoint: " + endpoint);
            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body());
            System.out.println("---");
            
        } catch (Exception e) {
            System.out.println("Error testing " + endpoint + ": " + e.getMessage());
        }
    }
}