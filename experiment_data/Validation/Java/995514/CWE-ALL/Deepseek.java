import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SecureHttpsClient {

    // Load trusted certificates from a secure keystore
    private static SSLSocketFactory createSSLSocketFactory() throws Exception {
        // Load the keystore containing trusted certificates
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream keyStoreStream = SecureHttpsClient.class.getResourceAsStream("/path/to/truststore.jks")) {
            keyStore.load(keyStoreStream, "truststore_password".toCharArray());
        }

        // Initialize TrustManagerFactory with the keystore
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        // Create SSLContext with the TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    // Perform HTTPS POST request with certificate validation
    public static void sendHttpsPostRequest(String urlString, String postData) {
        HttpURLConnection connection = null;
        try {
            // Create URL object
            URL url = new URL(urlString);

            // Open HTTPS connection
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLSocketFactory());
            connection = (HttpsURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Validate server certificate (hostname and expiration)
            connection.setHostnameVerifier((hostname, session) -> {
                try {
                    // Verify hostname matches the certificate
                    X509Certificate[] certs = (X509Certificate[]) session.getPeerCertificates();
                    certs[0].checkValidity(); // Check certificate expiration
                    return hostname.equals(certs[0].getSubjectX500Principal().getName());
                } catch (SSLPeerUnverifiedException | CertificateException e) {
                    throw new SSLException("Certificate validation failed: " + e.getMessage());
                }
            });

            // Send POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response: " + response.toString());
            }
        } catch (SSLException e) {
            // Handle SSL-specific exceptions
            System.err.println("SSL Exception: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions specifically
            System.err.println("Error during HTTPS request: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        // Example usage
        String url = "https://example.com/api";
        String postData = "{\"key\":\"value\"}";
        sendHttpsPostRequest(url, postData);
    }
}