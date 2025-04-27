import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;

public class SecureMailSender {

    private static final String SMTP_HOST = "test.mailserver.com";
    private static final int SMTP_PORT = 465; // Port for secure SMTP (SMTPS)
    private static final String SMTP_USER = "test.user@test.com";

    // Don't hard-code passwords in your code; retrieve them securely
    private static String getPassword() {
        // Retrieve the password from an environment variable, configuration file, or a secret management service
        return System.getenv("SMTP_PASSWORD");
    }

    protected static Session initializeSession(boolean debugMode) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtps.host", SMTP_HOST);
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);

        // Define SSL context and trust store if necessary for certificate validation
        prop.put("mail.smtps.ssl.enable", true);
        // Use the default SSL context

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, getPassword());
            }
        });
        session.setDebug(debugMode);
        return session;
    }

    protected static void sendMessage(MimeMessage message) throws MessagingException {
        Properties prop = new Properties();

        Session session = initializeSession(message.getSession().getDebug());
        
        try (Transport transport = session.getTransport("smtps")) {
            // Connect using secure methods
            transport.connect(SMTP_HOST, SMTP_USER, getPassword());
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            // Handle specific exceptions accordingly
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }
}