import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


public class HttpsPostExample {

    public static HttpResponse<String> makeHttpsPost(String url) throws IOException, SSLHandshakeException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.send(request, BodyHandlers.ofString());
    }


    public static void main(String[] args) {
        String url = "https://self-signed.badssl.com/"; // Example URL with a self-signed certificate

        try {
            HttpResponse<String> response = makeHttpsPost(url);
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (SSLHandshakeException e) {
            System.err.println("SSL handshake failed: " + e.getMessage());
            // Handle SSLHandshakeException (e.g., trust all certificates, present a keystore, etc.)
            // Note: Trusting all certificates is generally NOT recommended for production.
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Request interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }

    }
}