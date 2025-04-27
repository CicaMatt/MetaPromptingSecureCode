import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;

public class SecureHttpsConnection {

    public static void main(String[] args) {
        try {
            // Load the certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new FileInputStream("/path/to/certificate.pem");

            // Create a certificate
            X509Certificate caCert;
            try {
                caCert = (X509Certificate) cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", caCert);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Set as default
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // URL to the HTTPS server
            URL url = new URL("https://www.abc.com");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Set the request method and properties
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            // Get and send data
            try (OutputStream os = conn.getOutputStream()) {
                // Example for sending request if needed
                // String params = "param1=value1&param2=value2";
                // os.write(params.getBytes("UTF-8"));
                os.flush();
            }

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}