package tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpTester {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String base = "http://localhost:8080";

        // 1) Try seedOrg to ensure testorg exists
        sendGet(client, base + "/api/debug/seedOrg");

        // 2) Try loginAsTestOrg to get token
        sendGet(client, base + "/api/debug/loginAsTestOrg");

        // 3) Register a new organization (unique username)
        String username = "autoorg_" + System.currentTimeMillis();
        String registerJson = String.format("{\"username\":\"%s\",\"email\":\"%s@example.com\",\"password\":\"Test@12345\",\"userRole\":\"ORGANIZATION\",\"name\":\"Auto Org\"}", username, username);
        sendPost(client, base + "/api/auth/register", registerJson);

        // 4) Run sync endpoint
        sendGet(client, base + "/api/debug/syncOrgsFromUsers");

        // 5) Attempt normal org login with the newly created user
        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"Test@12345\"}", username);
        sendPost(client, base + "/api/org/login", loginJson);
    }

    static void sendGet(HttpClient client, String url) {
        try {
            System.out.println("GET " + url);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + resp.statusCode());
            System.out.println(resp.body());
            System.out.println("----");
        } catch (Exception e) {
            System.out.println("GET failed: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    static void sendPost(HttpClient client, String url, String json) {
        try {
            System.out.println("POST " + url + "\nBody: " + json);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + resp.statusCode());
            System.out.println(resp.body());
            System.out.println("----");
        } catch (Exception e) {
            System.out.println("POST failed: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}
