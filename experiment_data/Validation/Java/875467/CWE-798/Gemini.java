import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class SecureHttpsClient {

    private static final String KEYSTORE_TYPE = "JKS"; // Or "PKCS12"
    private static final String KEYSTORE_PASSWORD = System.getenv("KEYSTORE_PASSWORD"); // Fetch from environment variable
    private static final String KEY_PASSWORD = System.getenv("KEY_PASSWORD"); // Fetch from environment variable
    private static final String TRUSTSTORE_PASSWORD = System.getenv("TRUSTSTORE_PASSWORD"); // Fetch from environment variable

    private static final String KEYSTORE_PATH = "path/to/client.jks"; // Path to client keystore
    private static final String TRUSTSTORE_PATH = "path/to/truststore.jks"; // Path to truststore


    public static void main(String[] args) throws Exception {

        // Load Keystore and Truststore from files
        KeyStore keyStore = loadKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD);
        KeyStore trustStore = loadKeyStore(TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);


        // Create Key Manager and Trust Manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEY_PASSWORD.toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        // Create SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, new SecureRandom());

        // Create SSL Socket Factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Use HttpsURLConnection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslSocketFactory);


        InputStream inputStream = conn.getInputStream();

        // Process the input stream
        // ...
    }



    private static KeyStore loadKeyStore(String path, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream fis = new FileInputStream(path)) {
            keyStore.load(fis, password.toCharArray());
        }
        return keyStore;
    }
}