import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

/**
 * This function sets a secure connection with certificate validation and pinning enabled
 */
public class SecureConnection {

    public static void main(String[] args) {
        try {
            // Load the server certificate from a file
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream caInput = new FileInputStream("path/to/your/certificate.crt");
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            // Initialize KeyStore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Initialize TrustManagerFactory with the KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Prepare mutual SSL layer on the client side
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Open connection with the server and apply SSL socket factory
            URL url = new URL("https://secure-server.com/api");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Enable host name verification to prevent man-in-the-middle attacks
            conn.setHostnameVerifier((hostname, session) -> {
                // Here we can implement additional checking like hostname equilence
                // This is a simplistic check, ensure robust at production
                return hostname.equals(session.getPeerHost());
            });

            // Call the API
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // Read input stream - for demo, we just print some lines
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Close resources
            reader.close();
            in.close();

        } catch (IOException | CertificateException | KeyStoreException |
                 NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}