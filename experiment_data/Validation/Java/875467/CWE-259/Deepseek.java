import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class HttpsClientCertificateExample {

    public static void main(String[] args) throws Exception {
        // Load the server's root certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new FileInputStream("path/to/server-root-certificate.crt");
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(caInput);
        caInput.close();

        // Load the client certificate and private key
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream keyStoreInput = new FileInputStream("path/to/client-certificate.p12");
        keyStore.load(keyStoreInput, "client-cert-password".toCharArray());
        keyStoreInput.close();

        // Create a KeyManagerFactory and initialize it with the client certificate
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "client-cert-password".toCharArray());

        // Create a TrustManagerFactory and initialize it with the server's root certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("server-root-cert", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create an SSLContext and initialize it with the KeyManager and TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // Set the default SSLSocketFactory to use our SSLContext
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Create the HTTPS connection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response
        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }
}