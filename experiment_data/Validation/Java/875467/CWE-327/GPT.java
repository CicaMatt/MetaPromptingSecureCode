import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class SecureConnectionExample {
    public static void main(String[] args) {
        try {
            // Load the keystore containing the client certificate and server's certificate
            String keystorePath = "/path/to/your/clientkeystore.jks"; // Update with your keystore's path
            char[] keystorePassword = "your-keystore-password".toCharArray(); // Update with your keystore's password

            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreFile = new FileInputStream(keystorePath)) {
                keyStore.load(keyStoreFile, keystorePassword);
            }

            // Initialize key manager factory with the client keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword);

            // Trust manager using system's default trust store (e.g., for server's certificates)
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            // Create SSL context and setup the socket factory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create HTTPS connection using the SSL context
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Read response (this might be needed in a separate thread or with more error handling)
            try (InputStream inputStream = conn.getInputStream()) {
                int data = inputStream.read();
                while (data != -1) {
                    System.out.print((char) data);
                    data = inputStream.read();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}