import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static Certificate[] getTrustedRootCertificates() throws KeyStoreException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null); // Load the default truststore

        Certificate[] certificates = null;
        try {
            Enumeration<String> aliases = ks.aliases();
            int count = 0;
            while(aliases.hasMoreElements()){
                aliases.nextElement();
                count++;
            }

            certificates = new Certificate[count];
            aliases = ks.aliases(); // Reset the enumeration
            int i = 0;
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (ks.isCertificateEntry(alias)) {
                    certificates[i++] = ks.getCertificate(alias);
                }

            }
        } catch (java.io.IOException e){
            // This should theoretically never happen with a null input stream
            // But handle it gracefully for completeness
            System.err.println("Unexpected IO Exception reading truststore: " + e.getMessage());
           return new Certificate[0]; // return empty array
        }

        return certificates;

    }

    public static void main(String[] args) {
        try {
            Certificate[] trustedRoots = getTrustedRootCertificates();
            System.out.println("Trusted Root Certificates:");
            for (Certificate cert : trustedRoots) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    System.out.println("Subject: " + x509Cert.getSubjectDN());
                    System.out.println("Issuer: " + x509Cert.getIssuerDN());
                    System.out.println("-----");
                }
            }
        } catch (KeyStoreException e) {
            System.err.println("Error accessing keystore: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error with algorithm: " + e.getMessage());
        }
    }
}