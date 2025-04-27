import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SecureConnectionExample {

    public static void main(String[] args) {
        try {
            // URL to be accessed
            URL url = new URL("https://www.abc.com");

            // Open an HTTPS connection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Validate the server's certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            
            sslContext.init(null, new TrustManager[]{new TolerantTrustManager()}, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Connect to the server
            conn.connect();

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    System.out.println(responseLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom TrustManager for performing more tolerant server certificate validation
    private static class TolerantTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // Method left empty for client-side cert checking if needed
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // Implement your logic here to validate the server's certificate
            // Validate issuer, validity period, hostname, etc.
            // For demonstration, you can use a basic check
            for (X509Certificate cert : chain) {
                cert.checkValidity(); // Check the certificate's validity period
                // You can add code here to check issuer, public key, etc.
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}