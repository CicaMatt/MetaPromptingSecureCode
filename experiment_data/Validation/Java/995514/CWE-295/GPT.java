import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class SecureHttpsPost {

    public static void main(String[] args) {
        try {
            // Load the certificate file (ensure the path to your certificate is correct)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate ca;

            try (InputStream caInput = new FileInputStream("path/to/your/certificate.crt")) {
                ca = (X509Certificate) cf.generateCertificate(caInput);
                System.out.println("Loaded CA: " + ca.getSubjectDN());
            }

            // Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create an SSLContext that uses our uploaded TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Set the default SSL context to the one we've configured
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Connect to the server using HTTPS
            URL url = new URL("https://your-secure-url.com/api");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable certificate validation
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Prepare request data
            String jsonInputString = "{\"key\":\"value\"}";

            // Write to the connection
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code to ensure the request was processed correctly
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}