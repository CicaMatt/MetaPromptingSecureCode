import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.net.URL;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpClient {
    public static void main(String[] args) {
        try {
            // Trust all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    }
                }
            };

            // Initialize the SSL context
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create a URL and open a connection
            URL url = new URL("https://yourserver.com/yourEndpoint");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the connection properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json"); // Set the appropriate content type

            // Send the request data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = "{\"key\":\"value\"}".getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}