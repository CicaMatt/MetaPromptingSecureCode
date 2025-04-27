import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class HttpsClientCertificateExample {

    public static void main(String[] args) throws Exception {
        // Load the client certificate and its password from a secure source (e.g., environment variables)
        String clientCertPath = System.getenv("CLIENT_CERT_PATH");
        String clientCertPassword = System.getenv("CLIENT_CERT_PASSWORD");

        // Load the client certificate
        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(clientCertPath)) {
            clientKeyStore.load(fis, clientCertPassword.toCharArray());
        }

        // Initialize KeyManagerFactory with the client certificate
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, clientCertPassword.toCharArray());

        // Initialize TrustManagerFactory with the server's root certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null); // Initialize an empty trust store

        // Load the server's root certificate
        String serverRootCertPath = System.getenv("SERVER_ROOT_CERT_PATH");
        try (FileInputStream fis = new FileInputStream(serverRootCertPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
            trustStore.setCertificateEntry("serverRootCert", cert);
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Create and initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // Set the custom SSLSocketFactory to HttpsURLConnection
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Create the HTTPS connection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response
        try (InputStream inputStream = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}