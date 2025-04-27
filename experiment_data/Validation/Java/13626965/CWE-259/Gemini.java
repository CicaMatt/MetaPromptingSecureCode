import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class SecureHTTPRequest {

    public static void main(String[] args) throws IOException {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);



            URL url = new URL("https://www.abc.com"); // Replace with your URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // or POST as needed

            // If you need to send data with POST
            // conn.setDoOutput(true);
            // DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            // wr.writeBytes(params); // Make sure 'params' is correctly defined
            // wr.flush();
            // wr.close();


            // Read the response
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300 ) { // Check for success response codes
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder(); // More efficient string concatenation
                while ((line = br.readLine()) != null) {
                   response.append(line).append("\n");
                }
                br.close();
                System.out.println(response.toString());
            } else {
                System.err.println("HTTP Error: " + conn.getResponseCode() + " " + conn.getResponseMessage());

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println(errorLine);
                }
                errorReader.close();
            }

            conn.disconnect();

        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}