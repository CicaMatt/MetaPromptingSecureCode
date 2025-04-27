import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpExample {

    private static final Logger logger = Logger.getLogger(HttpExample.class.getName());

    public static void main(String[] args) {
        try {
            disableSSLVerification();  // Disables SSL verification temporarily for testing

            URL url = new URL("https://www.abc.com");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                // wr.writeBytes(params);
                wr.flush();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during output stream operations", e);
                return;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during input stream operations", e);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Connection setup failed", e);
        }
    }

    private static void disableSSLVerification() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Install a host name verifier that always returns true
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}