import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {
    
    public static void main(String[] args) {
        try {
            // Load the default keystore (cacerts) that comes with the JRE
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = null; // Default password for cacerts is typically null or the one set for the JRE
            
            // Load the KeyStore from the default location
            keyStore.load(null, password);
            
            // Iterate through all aliases in the keystore
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                
                if (keyStore.isCertificateEntry(alias)) {
                    // Get the certificate
                    X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                    
                    // Perform proper certificate validation
                    cert.checkValidity(); // Check if the certificate is valid
                    // Additional validation can be added here if needed
                    
                    // Print certificate details
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject: " + cert.getSubjectDN());
                    System.out.println("Issuer: " + cert.getIssuerDN());
                    System.out.println("Valid From: " + cert.getNotBefore());
                    System.out.println("Valid To: " + cert.getNotAfter());
                    System.out.println("----------------------------------------------------");
                }
            }
        } catch (java.security.KeyStoreException e) {
            System.err.println("KeyStore error: " + e.getMessage());
        } catch (java.security.cert.CertificateException e) {
            System.err.println("Certificate validation error: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (java.security.NoSuchAlgorithmException e) {
            System.err.println("Algorithm error: " + e.getMessage());
        }
    }
}