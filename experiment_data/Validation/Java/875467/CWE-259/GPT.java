import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class SecureHttpsClient {
    public static void main(String[] args) {
        try {
            // Load client certificate
            // Replace "your_certificate.p12" with the path to your client certificate file
            // Retrieve the password securely, e.g., from environment variables
            String keyStorePassword = System.getenv("KEYSTORE_PASSWORD");
            FileInputStream keyStoreStream = new FileInputStream("path/to/your_certificate.p12");

            // Initialize KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreStream, keyStorePassword.toCharArray());

            // Create KeyManagerFactory using the KeyStore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            // Create trust manager to handle self-signed certificates
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            // Initialize SSLContext with KeyManager and TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create URL connection
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Set connection timeouts
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            // Make request and read response
            try (InputStream responseStream = connection.getInputStream()) {
                // Handle the response stream as required
                // For demonstration, simply print out the content
                int data;
                while ((data = responseStream.read()) != -1) {
                    System.out.print((char) data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}