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
        InputStream serverCertInputStream = new FileInputStream("path/to/server-root-cert.crt");
        X509Certificate serverCert = (X509Certificate) cf.generateCertificate(serverCertInputStream);
        serverCertInputStream.close();

        // Load the client certificate and private key
        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
        InputStream clientCertInputStream = new FileInputStream("path/to/client-cert.p12");
        clientKeyStore.load(clientCertInputStream, "client-cert-password".toCharArray());
        clientCertInputStream.close();

        // Initialize the KeyManagerFactory with the client keystore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, "client-cert-password".toCharArray());

        // Initialize the TrustManagerFactory with the server's root certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("serverCert", serverCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create and initialize the SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // Set the custom SSLSocketFactory to HttpsURLConnection
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Create the URL connection
        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response
        InputStream inputstream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }
}