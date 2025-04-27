import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;

public class HttpsClientCertificateExample {

    public static void main(String[] args) throws Exception {
        // Load the client certificate and private key
        String clientKeystorePath = "/path/to/client_keystore.jks";
        String clientKeystorePassword = "client_keystore_password";
        String clientKeyPassword = "client_key_password";

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(clientKeystorePath)) {
            clientKeyStore.load(fis, clientKeystorePassword.toCharArray());
        }

        // Initialize KeyManagerFactory with the client keystore
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, clientKeyPassword.toCharArray());

        // Load the server's root certificate
        String serverCertPath = "/path/to/server_root_certificate.crt";
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate serverCert;
        try (FileInputStream fis = new FileInputStream(serverCertPath)) {
            serverCert = (X509Certificate) certificateFactory.generateCertificate(fis);
        }

        // Load the truststore and add the server's root certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null); // Initialize an empty truststore
        trustStore.setCertificateEntry("serverCert", serverCert);

        // Initialize TrustManagerFactory with the truststore
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
        try (InputStream inputStream = conn.getInputStream()) {
            int data;
            while ((data = inputStream.read()) != -1) {
                System.out.print((char) data);
            }
        }
    }
}