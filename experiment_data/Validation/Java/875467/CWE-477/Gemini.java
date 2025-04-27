import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsClient {

    public static void main(String[] args) throws Exception {
        // Keystore containing the client certificate and private key
        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12"); // Or "JKS" if not PKCS12
        clientKeyStore.load(HttpsClient.class.getResourceAsStream("client.p12"), "clientPassword".toCharArray()); // Replace with your client keystore path and password

        // Truststore containing the server's root certificate
        KeyStore trustStore = KeyStore.getInstance("JKS"); // Or the appropriate type for your truststore
        trustStore.load(HttpsClient.class.getResourceAsStream("truststore.jks"), "truststorePassword".toCharArray()); // Replace with your truststore path and password


        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, "clientPassword".toCharArray()); // Password for the client private key

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);


        SSLContext sslContext = SSLContext.getInstance("TLS"); // Or "SSL" if necessary, but TLS is preferred
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();

        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslsocketfactory);


        // Set Hostname verification (CRITICAL for security)
        // Java 6 does not have the SNI extension.  This workaround helps to mitigate
        // the issue but isn't a full solution. Upgrading to a newer Java version is
        // strongly recommended for proper SNI support and overall security.
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                try {
                    X509Certificate[] certs = (X509Certificate[]) session.getPeerCertificates();
                    X509Certificate serverCert = certs[0];
                    return hostname.equalsIgnoreCase(serverCert.getSubjectDN().getName());
                    // OR
                    // return true; //  Disable hostname verification (Highly Discouraged)
                } catch (CertificateException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });

        InputStream inputstream = conn.getInputStream();


        // Process the inputstream
        // ...

    }
}