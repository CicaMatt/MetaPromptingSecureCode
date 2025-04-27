import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.cert.X509Certificate;

public class SecureHTTPS {

    public static void main(String[] args) {
        try {
            // Configure the URL
            URL url = new URL("https://your-https-url.com");

            // Create a trust-all SSL context (ONLY for testing/development - NOT for production)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());


            // Create HTTPS connection
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory()); // Use custom SSL context
            connection.setHostnameVerifier(new TrustAllHostnameVerifier());  // Use custom hostname verifier
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set request headers (if needed)
            connection.setRequestProperty("Content-Type", "application/json");


            // Write POST data
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("your_post_data"); // Replace with your actual POST data
            writer.flush();
            writer.close();


            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // WARNING: This is for testing/development ONLY. NEVER use this in production.
    private static class TrustAllX509TrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }


    // WARNING: This is for testing/development ONLY. NEVER use this in production.
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


}