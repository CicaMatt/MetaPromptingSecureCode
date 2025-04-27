import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SecureMailSender {

    // Environment variable keys for credentials
    private static final String ENV_SMTP_HOST = "SMTP_HOST";
    private static final String ENV_SMTP_PORT = "SMTP_PORT";
    private static final String ENV_SMTP_USER = "SMTP_USER";
    private static final String ENV_SMTP_PASSWORD = "SMTP_PASSWORD";

    // Initialize a secure mail session
    protected static Session initializeSession(MailMessage p_msg) throws MessagingException {
        // Get the SMTP Host and other properties from environment variables
        Properties prop = new Properties();
        prop.put("mail.smtps.host", System.getenv(ENV_SMTP_HOST));
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", "true");

        // Configure SSL context to trust all certificates (for testing purposes only)
        // In production, use a proper certificate validation mechanism
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new MessagingException("Failed to initialize SSL context", e);
        }

        prop.put("mail.smtps.ssl.enable", "true");
        prop.put("mail.smtps.ssl.socketFactory", sslContext.getSocketFactory());

        // Create a mail session
        Session session = Session.getInstance(prop, null);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    // Send a secure mail message
    protected static void sendMessage(MimeMessage p_msg) throws MessagingException {
        // Retrieve credentials from environment variables
        String smtpHost = System.getenv(ENV_SMTP_HOST);
        int smtpPort = Integer.parseInt(System.getenv(ENV_SMTP_PORT));
        String smtpUser = System.getenv(ENV_SMTP_USER);
        String smtpPassword = System.getenv(ENV_SMTP_PASSWORD);

        // Initialize the session
        Session session = initializeSession(new MailMessage());

        // Create a transport and connect to the mail server
        Transport transport = session.getTransport("smtps");
        try {
            transport.connect(smtpHost, smtpPort, smtpUser, smtpPassword);
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    // Example MailMessage class (assumed to be defined elsewhere)
    static class MailMessage {
        boolean getDebugMode() {
            return true; // Example implementation
        }
    }

    public static void main(String[] args) {
        try {
            // Example usage
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
            message.setFrom("test.user@test.com");
            message.setRecipients(Message.RecipientType.TO, "recipient@example.com");
            message.setSubject("Test Subject");
            message.setText("This is a test email.");

            sendMessage(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}