import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class SecureHttpsClient {

    private static final String KEYSTORE_TYPE = "PKCS12"; // Or "JKS" if using JKS keystore
    private static final String KEYSTORE_PASSWORD = getSecurePassword("KEYSTORE_PASSWORD"); // Retrieve from secure config
    private static final String CLIENT_CERTIFICATE_ALIAS = "client_cert"; // Alias of client certificate in keystore
    private static final String CLIENT_CERTIFICATE_PASSWORD = getSecurePassword("CLIENT_CERTIFICATE_PASSWORD"); // Retrieve from secure config


    public static void main(String[] args) throws Exception {

        // Load Keystore and Truststore
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(new FileInputStream("client.p12"), KEYSTORE_PASSWORD.toCharArray()); // Path to client keystore

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream("truststore.jks"), getSecurePassword("TRUSTSTORE_PASSWORD").toCharArray()); // Path to truststore containing server's root certificate


        // Create Key Manager Factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, CLIENT_CERTIFICATE_PASSWORD.toCharArray());

        // Create Trust Manager Factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);


        // Create SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLS"); // Use TLS instead of SSL
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        // Create SSL Socket Factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Create HTTPS Connection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslSocketFactory);

        // Handle specific exceptions
        try (InputStream inputStream = conn.getInputStream()) {
            // Process the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
           handleException(e);
        } catch (SSLHandshakeException e) {
            handleException(e);
        }
    }


    private static String getSecurePassword(String environmentVariable) {
        // Implement secure password retrieval from configuration files, environment variables, or a dedicated secrets management system
        // Placeholder implementation for demonstration purposes. NEVER hardcode passwords in real-world applications.
        return System.getenv(environmentVariable); 
    }


    private static void handleException(Exception e) throws Exception {
         // Implement proper exception handling and logging
        e.printStackTrace(); // For debugging during development. Replace with proper logging in production.
        throw e; // Re-throw after logging. Alternatively, handle differently based on exception type.
    }

}