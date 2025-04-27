import javax.net.ssl.*;
import java.security.KeyStore;
import java.io.*;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;

public class SSLUtil {

    public static void main(String[] args) {
        try {
            // Path to the certificate file and password
            String certFilePath = "path/to/your/server/certificate.crt";
            String password = "changeit";  // Default Java keystore password

            // Create the KeyStore and load the certificate
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(certFilePath)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate cert = cf.generateCertificate(fis);

                trustStore.load(null, null);  // Initialize trustStore
                trustStore.setCertificateEntry("server", cert);
            }

            // Create a TrustManager that trusts the certificate in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Initialize SSLContext with TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Set the default SSL context
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Example URL connection
            URL url = new URL("https://your-secure-server.com");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            
            // Add any additional headers or data as needed
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write("data=example".getBytes());
            os.flush();
            os.close();

            // Check the response
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}