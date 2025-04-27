import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.KeyManagerFactory;

public class HttpsClient {

    private static final Logger logger = Logger.getLogger(HttpsClient.class.getName());

    public static void main(String[] args) {
        try {
            // Initialize SSL context with a TrustManager that accepts all certificates
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType)
                            throws CertificateException {
                        // Trust all client certificates
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType)
                            throws CertificateException {
                        // Trust all server certificates
                    }
                }
            };
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

            // Set the default SSL context to accept all certificates
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // URL for your HTTPS connection
            URL url = new URL("https://yourserver.com/api/post");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set request properties and method
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Example data to send
            String jsonInputString = "{\"key\": \"value\"}";

            // Send POST request
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            // Read the response
            try (InputStream is = connection.getInputStream()) {
                // Handle the response
                System.out.println("Response Code: " + connection.getResponseCode());
            }

        } catch (javax.net.ssl.SSLException e) {
            logger.log(Level.SEVERE, "SSL Exception: Not trusted server certificate!", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O Exception occurred during the HTTPS post request.", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred.", e);
        }
    }
}