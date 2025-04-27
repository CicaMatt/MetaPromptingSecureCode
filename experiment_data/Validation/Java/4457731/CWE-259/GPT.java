import javax.mail.*;
import javax.mail.internet.*;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.ssl.*;

public class SecureMailClient {

    // A method to initialize the mail session
    protected static Session initializeSession(MailMessage p_msg, String host, String port) throws Exception {
        // Load system properties and set up mail server configurations
        Properties prop = new Properties();
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Retrieve encrypted email credentials
        String email = CredentialProvider.getEmail();
        String password = CredentialProvider.getPassword();

        // Create an Authenticator object for the session
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        // Get the default Session object with authentication
        Session session = Session.getInstance(prop, auth);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    // A helper method to send a message
    protected static void sendMessage(MimeMessage p_msg, Session session) throws Exception {
        // Create a transport object and connect with the email credentials
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect();
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        }
    }

    // Main method or other parts of your application can use this method
    public static void main(String[] args) {
        try {
            MailMessage p_msg = new MailMessage(); // Assume this is setup properly
            Session session = initializeSession(p_msg, "test.mailserver.com", "587");
            sendMessage(p_msg, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mocked credential provider class
    static class CredentialProvider {
        // Here you would securely fetch and decrypt your credentials
        public static String getEmail() {
            // Implementation to retrieve the email
            return "secure_email@example.com";
        }

        public static String getPassword() {
            // Implementation to retrieve the encrypted password
            return "securePassword";
        }
    }
}