import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SecureHttpsPost {

    public static void main(String[] args) {
        try {
            // Set up a secure connection with a TrustManager
            TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        // Implement client certificate checks if needed
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        // Here you can manually verify the server's certificate against a known good certificate
                        // e.g., by comparing it with a stored copy or a specific public key
                    }
                }
            };

            // Initialize SSL context with the trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());

            // Create a secure connection
            URL url = new URL("https://yourserver.com/api");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            
            // Optionally confirm host name verification
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                    // Verify host name or return true to trust all host names (not recommended for production)
                    return "yourserver.com".equalsIgnoreCase(hostname);
                }
            });

            // Configure the connection for a POST request
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Write your message body
            String jsonInputString = "{\"key\": \"value\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code (200 is OK)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Additional processing of response if necessary

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}