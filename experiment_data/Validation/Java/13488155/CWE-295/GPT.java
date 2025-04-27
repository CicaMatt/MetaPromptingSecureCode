import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class SecureConnection {

    public static void main(String[] args) {
        try {
            // Load the trusted certificate from the keystore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream keyStoreStream = SecureConnection.class.getResourceAsStream("/path/to/your/keystore")) {
                keyStore.load(keyStoreStream, "keystore-password".toCharArray());
            }

            // Set up a TrustManager that trusts the certificate in our keystore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

            // Open a connection to the server
            URL url = new URL("https://your.secure.server");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            
            // Verify the server's certificate (Implementing hostname verification)
            connection.setHostnameVerifier((hostname, session) -> {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("your.secure.server", session);
            });

            // Communicate with the server securely
            try (InputStream in = connection.getInputStream()) {
                // Perform I/O operations with the secure connection
                // For example, reading the input stream
                int data = in.read();
                while (data != -1) {
                    System.out.print((char) data);
                    data = in.read();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}