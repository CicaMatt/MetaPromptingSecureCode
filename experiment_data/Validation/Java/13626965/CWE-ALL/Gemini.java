import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class SecureHTTPSRequest {

    public static void main(String[] args) {
        try {
            // Configure TLS settings to trust all certificates (INSECURE - FOR TESTING ONLY)
            // In a production environment, properly validate certificates as shown below
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new TrustAllHostnameVerifier());


            URL url = new URL("https://www.abc.com");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            // Proper Certificate Validation (RECOMMENDED FOR PRODUCTION)
            // validateCertificates(conn);


            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            conn.disconnect();


        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            // Handle specific exceptions
            if (e instanceof SSLHandshakeException) {
                System.err.println("SSL Handshake failed: " + e.getMessage());
                // Log the detailed exception and potentially retry with a custom TrustManager
                // after further investigation of the certificate issue.
            } else {
                System.err.println("Error during HTTPS request: " + e.getMessage());
                e.printStackTrace(); //Only for debugging, remove in production.
            }
            
        } 
    }

     //Example (INCOMPLETE - Requires proper TrustStore configuration) 
     private static void validateCertificates(HttpsURLConnection conn) throws SSLHandshakeException {
        try {
            SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory(); // Replace with custom factory if needed.
            conn.setSSLSocketFactory(factory);
        } catch (Exception ex) {
           throw new SSLHandshakeException("Certificate validation failed: " + ex.getMessage());
        }
     }

    // INSECURE - FOR TESTING ONLY
    private static class TrustAllX509TrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }

    // INSECURE - FOR TESTING ONLY
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true; // Accept all hostnames
        }
    }

}