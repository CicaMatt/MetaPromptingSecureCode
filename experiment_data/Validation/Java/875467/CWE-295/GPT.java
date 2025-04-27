import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class SecureHttpsConnection {

    public static void main(String[] args) {
        try {
            // Load client certificate
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream("/path/to/client_certificate.p12")) {
                clientStore.load(fis, "client_password".toCharArray());
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, "client_password".toCharArray());

            // Load trusted server certificate
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream("/path/to/truststore.jks")) {
                trustStore.load(fis, "truststore_password".toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Set up the SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Create the connection
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Additional connection settings
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            // Open the connection and read the response
            try (InputStream response = conn.getInputStream()) {
                // Process the response
                int data;
                while ((data = response.read()) != -1) {
                    System.out.print((char) data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}