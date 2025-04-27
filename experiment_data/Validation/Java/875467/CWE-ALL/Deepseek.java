import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class SecureHttpsClient {

    public static void main(String[] args) {
        try {
            // Load the server's root certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream rootCertFile = new FileInputStream("path/to/server-root-cert.crt");
            X509Certificate rootCert = (X509Certificate) cf.generateCertificate(rootCertFile);
            rootCertFile.close();

            // Load the client certificate and private key from a separate keystore
            KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
            char[] clientKeyStorePassword = System.getenv("CLIENT_KEYSTORE_PASSWORD").toCharArray();
            FileInputStream clientKeyStoreFile = new FileInputStream("path/to/client-keystore.p12");
            clientKeyStore.load(clientKeyStoreFile, clientKeyStorePassword);
            clientKeyStoreFile.close();

            // Initialize the KeyManagerFactory with the client keystore
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, clientKeyStorePassword);

            // Initialize the TrustManagerFactory with the server's root certificate
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("serverRootCert", rootCert);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Create and configure the SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            // Set the custom SSLSocketFactory to the HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create the HTTPS connection
            URL url = new URL("https://somehost.dk:3049");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            InputStream inputstream = conn.getInputStream();
            int data;
            while ((data = inputstream.read()) != -1) {
                System.out.print((char) data);
            }
            inputstream.close();

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 UnrecoverableKeyException | KeyManagementException e) {
            // Handle specific exceptions appropriately
            e.printStackTrace();
        }
    }
}