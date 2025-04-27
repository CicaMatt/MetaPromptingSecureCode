import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class HttpsClientCert {

    public static void main(String[] args) throws Exception {

        // Keystore and truststore paths (SHOULD NOT BE HARDCODED in real applications)
        // Use environment variables or configuration files for these.
        String keyStorePath = System.getenv("CLIENT_KEYSTORE_PATH");  // e.g., /path/to/client.jks
        String keyStorePassword = System.getenv("CLIENT_KEYSTORE_PASSWORD");
        String trustStorePath = System.getenv("TRUSTSTORE_PATH"); // e.g., /path/to/cacerts
        String trustStorePassword = System.getenv("TRUSTSTORE_PASSWORD");

        if (keyStorePath == null || keyStorePassword == null || trustStorePath == null || trustStorePassword == null) {
            throw new IllegalArgumentException("Keystore and truststore paths and passwords must be provided as environment variables.");
        }


        // Load Keystore and Truststore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream keyStoreStream = new FileInputStream(keyStorePath)) {
            keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
        }


        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream trustStoreStream = new FileInputStream(trustStorePath)) {
            trustStore.load(trustStoreStream, trustStorePassword.toCharArray());
        }


        // Create Key Manager Factory and Trust Manager Factory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray()); // Use the password from secure storage
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();


        // Create SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLS"); // Or "TLSv1.2" for better security
        sslContext.init(keyManagers, trustManagers, new SecureRandom());


        // Create SSL Socket Factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


        // Make HTTPS Connection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslSocketFactory);


        // Read Response
        try (InputStream inputStream = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (SSLHandshakeException e) {
            System.err.println("SSL Handshake failed: " + e.getMessage());
            e.printStackTrace();
        //Handle other potential exceptions like IOException
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }


    }
}