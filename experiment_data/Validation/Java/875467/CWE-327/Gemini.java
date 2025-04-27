import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class SecureHttpsClient {

    public static void main(String[] args) throws Exception {

        // Keystore and truststore paths. Adjust as needed.
        String keyStorePath = "client.jks"; // Client keystore with private key and cert
        String keyStorePassword = "client_password";
        String trustStorePath = "truststore.jks"; // Truststore with server's root certificate
        String trustStorePassword = "truststore_password";


        // Load KeyStore and TrustStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(SecureHttpsClient.class.getResourceAsStream(keyStorePath), keyStorePassword.toCharArray());

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(SecureHttpsClient.class.getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());


        // Create KeyManagerFactory and TrustManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);


        // Create SSLContext with TLSv1.2 (or higher if available)
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");  // Or TLSv1.3
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);



        // Create SSLSocketFactory
        SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();

        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslsocketfactory);



        // Now, connect and process the response
        try (InputStream inputstream = conn.getInputStream()) {
            // Process the inputstream from the server.
            // ... Read data from inputstream ...
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
}