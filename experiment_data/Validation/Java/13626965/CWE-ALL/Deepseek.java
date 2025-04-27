import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

public class SecureHttpClient {

    public static void main(String[] args) {
        try {
            // Create a URL object for the target server
            URL url = new URL("https://www.abc.com");

            // Open a connection to the server
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Enable output for the connection (if needed for POST requests)
            conn.setDoOutput(true);

            // Create a custom TrustManager that trusts all certificates (NOT RECOMMENDED FOR PRODUCTION)
            // This is a temporary workaround for testing purposes only.
            // In production, use a valid certificate and proper certificate validation.
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Initialize the SSLContext with the custom TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Set the SSLContext to the connection
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Send the request (for POST requests, uncomment the following lines)
            // DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            // wr.writeBytes(params);
            // wr.flush();
            // wr.close();

            // Read the response from the server
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();

        } catch (Exception e) {
            // Handle specific exceptions instead of catching generic Exception
            if (e instanceof javax.net.ssl.SSLHandshakeException) {
                System.err.println("SSL Handshake Failed: " + e.getMessage());
            } else {
                System.err.println("An error occurred: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }
}