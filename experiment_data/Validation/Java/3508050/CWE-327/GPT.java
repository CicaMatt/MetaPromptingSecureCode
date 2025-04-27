import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Load the default system trust store
            String javaHome = System.getProperty("java.home");
            String trustStorePath = javaHome + "/lib/security/cacerts";
            FileInputStream is = new FileInputStream(trustStorePath);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = "changeit".toCharArray(); // Default password for the JRE cacerts keystore
            trustStore.load(is, password);

            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = trustStore.getCertificate(alias);

                if (cert instanceof X509Certificate) {
                    X509Certificate x509cert = (X509Certificate) cert;
                    System.out.println("Certificate Subject: " + x509cert.getSubjectDN());
                    System.out.println("Certificate Issuer: " + x509cert.getIssuerDN());
                    System.out.println();
                }
            }

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}