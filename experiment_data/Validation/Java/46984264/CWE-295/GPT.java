import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SecureConnectionExample {

    public static void main(String[] args) {
        String httpsURL = "https://example.com/api";
        
        try {
            // Create a trust manager that validates certificate chains properly
            TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // You can add additional logic here if client validation is required
                    }
                    
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Implement server certificate validation logic here
                        if (chain == null || chain.length == 0) {
                            throw new IllegalArgumentException("null or empty certificate chain");
                        }
                        if (authType == null || authType.isEmpty()) {
                            throw new IllegalArgumentException("null or empty authType");
                        }
                        // Use a default TrustManager to validate the server certificates
                        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        tmf.init((java.security.KeyStore) null);
                        
                        for (TrustManager tm : tmf.getTrustManagers()) {
                            ((X509TrustManager) tm).checkServerTrusted(chain, authType);
                        }
                    }
                }
            };

            // Install the custom TrustManager to SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());

            // Open the HTTPS URL connection
            URL url = new URL(httpsURL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Ensure Hostname verification is done
            connection.setHostnameVerifier((hostname, session) -> {
                // Allow only specific domain or more robust verification logic
                SSLSession defaultSession = HttpsURLConnection.getDefaultSSLSocketFactory().createSocket(hostname, 443).getSession();
                return HttpsURLConnection.getDefaultHostnameVerifier().verify("expected-hostname.com", defaultSession);
            });

            // Connect and read response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Connection successful and server certificate validated.");
                // Implement reading logic for response here
            } else {
                System.out.println("Failed to connect, response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}