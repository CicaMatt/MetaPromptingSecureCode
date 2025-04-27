import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class SecureHttpsClient {

    public static void main(String[] args) {
        try {
            // Load keystore containing client certificate
            String keystorePath = System.getenv("KEYSTORE_PATH"); // Path to your keystore
            String keystorePassword = System.getenv("KEYSTORE_PASSWORD"); // Keystore password
            if (keystorePath == null || keystorePassword == null) {
                throw new IllegalStateException("Keystore path or password is not set");
            }

            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreInput = new FileInputStream(keystorePath)) {
                keyStore.load(keyStoreInput, keystorePassword.toCharArray());
            }

            // Set up key managers to do client authentication
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            // Load truststore containing server certificate
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustStoreInput = new FileInputStream(keystorePath)) {
                trustStore.load(trustStoreInput, keystorePassword.toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Open a connection using the SSL context
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Perform the SSL handshake and get the data
            try (InputStream inputStream = conn.getInputStream()) {
                // Read the response...
                int data;
                while ((data = inputStream.read()) != -1) {
                    System.out.print((char) data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Proper exception handling
        }
    }
}