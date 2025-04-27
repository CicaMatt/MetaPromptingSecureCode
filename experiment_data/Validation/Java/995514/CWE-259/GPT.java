import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecureHttpClient {

    public static void main(String[] args) {
        String httpsUrl = "https://example.com/api";

        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustAllCerts = getTrustAllCertsManager();
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Opening the connection to the server
            URL url = new URL(httpsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setting the request method to POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);  // To send a request body

            // Optional: Add headers and body if needed
            connection.setRequestProperty("Content-Type", "application/json");
            String jsonInputString = "{\"key\": \"value\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Reading the response
            int responseCode = connection.getResponseCode();
            InputStream responseStream = responseCode == 200 ? 
                                         connection.getInputStream() : 
                                         connection.getErrorStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            System.out.println("Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TrustManager[] getTrustAllCertsManager() {
        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
    }
}