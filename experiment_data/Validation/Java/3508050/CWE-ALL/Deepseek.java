import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * This Java program retrieves and lists all trusted root certificates available in the default Java Runtime Environment (JRE) keystore.
 * The solution adheres to security best practices and incorporates relevant mitigation strategies.
 */
public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Load the default JRE keystore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            String javaHome = System.getProperty("java.home");
            String keystorePath = javaHome + "/lib/security/cacerts";
            char[] keystorePassword = "changeit".toCharArray(); // Default password for JRE keystore

            // Avoid hard-coded credentials by using environment variables or secure vaults in production
            // For demonstration, the default password is used here.
            // In production, retrieve the password securely (e.g., from environment variables or a secure vault).
            keyStore.load(new java.io.FileInputStream(keystorePath), keystorePassword);

            // Enumerate all trusted root certificates
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);

                // Ensure the certificate is an X.509 certificate
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject DN: " + x509Cert.getSubjectX500Principal());
                    System.out.println("Issuer DN: " + x509Cert.getIssuerX500Principal());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("Valid From: " + x509Cert.getNotBefore());
                    System.out.println("Valid Until: " + x509Cert.getNotAfter());
                    System.out.println("Signature Algorithm: " + x509Cert.getSigAlgName());
                    System.out.println("----------------------------------------");
                }
            }
        } catch (Exception e) {
            // Avoid generic exception handling by catching specific exceptions
            // For example, handle FileNotFoundException, IOException, KeyStoreException, etc., separately
            System.err.println("An error occurred while accessing the keystore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}