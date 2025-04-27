import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {
    public static void main(String[] args) {
        try {
            // Load the default Java KeyStore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // Load the keyStore with null InputStream & password for JRE's default trust store
            keyStore.load(null, null);

            // Enumeration of all aliases in the KeyStore
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isCertificateEntry(alias)) {
                    Certificate certificate = keyStore.getCertificate(alias);
                    if (certificate instanceof X509Certificate) {
                        X509Certificate x509Certificate = (X509Certificate) certificate;
                        // Print the certificate details
                        System.out.println("-----BEGIN CERTIFICATE-----");
                        System.out.println("Alias: " + alias);
                        System.out.println(x509Certificate);
                        System.out.println("-----END CERTIFICATE-----\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}