import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class SecureHttpsPost {

    private static final String SERVER_CERTIFICATE_PATH = "path/to/server.cer"; // Replace with the path to your server certificate
    private static final String USERNAME = ""; // Retrieve username from secure configuration (e.g., environment variable)
    private static final String PASSWORD = "";  // Retrieve password from secure configuration (e.g., secrets manager)

    public static void main(String[] args) throws Exception {

        // Load the server's certificate
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null); // Initialize an empty keystore

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(SERVER_CERTIFICATE_PATH);
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(fis);
        fis.close();

        keyStore.setCertificateEntry("server", certificate);

        // Create a TrustManager that trusts the custom certificate
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create an SSLContext using the custom TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);


        // Create URL and connection
        URL url = new URL("https://your-server-url");  // Replace with your server URL
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Set SSLSocketFactory
        connection.setSSLSocketFactory(sslContext.getSocketFactory());

        // Set basic authentication (if required)
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

        // Set request method and headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json"); // Or appropriate content type


        // Write request body (if any)
        String requestBody = "{ \"key\": \"value\" }"; // Example JSON request body
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = br.readLine();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            // Handle error response (e.g., 4xx or 5xx codes)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String errorResponse = br.readLine();
                System.err.println("Error Response: " + errorResponse);
            }
        }
    }
}