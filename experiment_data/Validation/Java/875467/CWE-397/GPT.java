import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class SecureHttpsClient {

    public static void main(String[] args) {
        try {
            // Load the client certificate and private key into a KeyStore
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream("path/to/your/client_certificate.p12")) {
                clientStore.load(fis, "clientCertPassword".toCharArray());
            }

            // Initialize a KeyManagerFactory with the client key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, "clientCertPassword".toCharArray());

            // Load the CA certificate into a trust store
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream("path/to/your/truststore.jks")) {
                trustStore.load(fis, "trustStorePassword".toCharArray());
            }

            // Initialize a TrustManagerFactory with the trusted CA certificates
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Set up an SSLContext with the key and trust managers
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            // Open a connection to the URL
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            
            // Set the custom SSLSocketFactory
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Read the response to ensure the connection works
            try (InputStream inputstream = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException | KeyManagementException | SSLHandshakeException e) {
            // Handle specific exceptions for better error management
            System.err.println("Error setting up SSL connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}