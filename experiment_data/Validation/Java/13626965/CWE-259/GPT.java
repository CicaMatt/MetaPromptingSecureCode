import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;

public class SecureHttpClient {

    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.abc.com");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Use a custom TrustManager or load a trusted certificate
            setupTrustStore(conn);
            
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            // Send request (if needed for POST, PUT, etc.)
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                // Send request payload if required
                // wr.writeBytes(params);
                wr.flush();
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupTrustStore(HttpsURLConnection connection) throws Exception {
        // Load the default trust store (or specify a custom one)
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var trustStream = SecureHttpClient.class.getResourceAsStream("/path/to/your/truststore.jks")) {
            trustStore.load(trustStream, "your-truststore-password".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        connection.setSSLSocketFactory(sslContext.getSocketFactory());

        // Optionally disable hostname verification, but not recommended for production
        // connection.setHostnameVerifier((hostname, session) -> true);
    }
}