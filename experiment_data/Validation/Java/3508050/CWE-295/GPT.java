import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;

public class SecureCertificateValidation {

    public static void main(String[] args) throws Exception {
        // Access the default Java KeyStore that contains the trusted root certificates
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(System.getProperty("java.home") +
                "/lib/security/cacerts")) {
            keyStore.load(fis, "changeit".toCharArray());
        }

        // Display all trusted certificates from KeyStore
        listTrustedCertificates(keyStore);

        // Set up a TrustManager with default certificates and custom validation logic
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        
        if (trustManagers.length == 0 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new NoSuchAlgorithmException("No X509TrustManager found");
        }

        X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];
        
        // Set up SSL context with custom trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new CustomTrustManager(x509TrustManager) }, null);

        // Configure SSL socket factory with the SSL context
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        
        // Then you can use HttpsURLConnection or other HTTPS clients with secure trust management
    }

    private static void listTrustedCertificates(KeyStore keyStore) throws Exception {
        for (String alias : keyStore.aliases()) {
            Certificate cert = keyStore.getCertificate(alias);
            if (cert instanceof X509Certificate) {
                X509Certificate x509Cert = (X509Certificate) cert;
                System.out.println("Trusted Certificate: " + x509Cert.getSubjectX500Principal());
            }
        }
    }

    // Custom TrustManager for enhanced certificate validation
    static class CustomTrustManager implements X509TrustManager {
        private final X509TrustManager defaultTrustManager;

        public CustomTrustManager(X509TrustManager defaultTrustManager) {
            this.defaultTrustManager = defaultTrustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            // Implement custom client certificate checks if necessary
            defaultTrustManager.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            // Basic validation with additional checks
            defaultTrustManager.checkServerTrusted(chain, authType);

            // Perform extra validations on the server certificate
            X509Certificate serverCert = chain[0];
            // Example: Validate certificate-specific fields, pinning, hostname verification, etc.
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return defaultTrustManager.getAcceptedIssuers();
        }
    }
}