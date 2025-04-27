import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class HttpsClient {

    public static void main(String[] args) {
        try {
            InputStream inputStream = connectToHttpsServer("somehost.dk", 3049, "keystore.jks", "keystorePassword");
            // Process inputStream
            inputStream.close(); // Important to close the stream
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        } catch (GeneralSecurityException e) {
            System.err.println("Security Exception: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }


    public static InputStream connectToHttpsServer(String host, int port, String keystorePath, String keystorePassword) throws IOException, GeneralSecurityException {

        // Load Keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(HttpsClient.class.getClassLoader().getResourceAsStream(keystorePath), keystorePassword.toCharArray()); // Load from resources

        // Create Key Manager Factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();


        // Create Trust Manager Factory (for self-signed certificates)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore); // Use the same keystore for trust
        TrustManager[] trustManagers = tmf.getTrustManagers();

        // Create SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLS"); // or SSL
        sslContext.init(keyManagers, trustManagers, null);

        // Create SSL Socket Factory
        SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();

        // Use HttpsURLConnection (preferred)
        URL url = new URL("https://" + host + ":" + port);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslsocketfactory);

        return conn.getInputStream();
    }
}