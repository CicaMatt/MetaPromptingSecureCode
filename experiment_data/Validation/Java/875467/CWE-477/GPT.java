import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class SecureHttpsConnection {

    public static void main(String[] args) {
        try {
            String keystorePath = "/path/to/keystore";  // Change this path to your keystore location
            char[] keystorePassword = "keystorePassword".toCharArray(); // Use the actual password for your keystore
            char[] keyPassword = "keyPassword".toCharArray(); // Use the actual password for your key

            // Load the keystore containing the client certificate
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreFile = new FileInputStream(keystorePath)) {
                keyStore.load(keyStoreFile, keystorePassword);
            }

            // Initialize KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyPassword);

            // Initialize TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create the SSLContext with the key managers and trust managers
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Open HTTPS connection with custom SSLSocketFactory
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Optional: Set other connection parameters like timeout, request method, etc.
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            // Read and process the input stream (e.g., print or save the content)
            // Important: Close the input stream and the connection after processing
            inputStream.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}