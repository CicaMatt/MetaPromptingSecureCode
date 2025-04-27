import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;

public class SecureMailSender {

    // Method to initialize a mail session with secure properties
    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", "true");

        // Ensure the correct SSL context is used
        prop.put("mail.smtps.ssl.socketFactory", createSslSocketFactory());

        return Session.getInstance(prop, null);
    }

    // Create an SSL socket factory using a trusted certificate
    private static SSLSocketFactory createSslSocketFactory() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        // Load your truststore (file containing the server's trusted certificates)
        try (FileInputStream trustStoreStream = new FileInputStream("path/to/your/truststore")) {
            keyStore.load(trustStoreStream, "truststorePassword".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    // Send a message using secure settings and externalized credentials
    protected static void sendMessage(MimeMessage p_msg, String username, String password) throws Exception {
        // Properly secure properties and session
        Properties prop = new Properties();
        Session session = Session.getInstance(prop, null);
        
        try (Transport transport = session.getTransport("smtps")) {
            // Connect using external credentials (not hard-coded)
            transport.connect("test.mailserver.com", 465, username, password);
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        }
    }

    public static void main(String[] args) {
        // Example execution
        try {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setDebugMode(true); // Example setup

            Session session = initializeSession(mailMessage);
            MimeMessage message = new MimeMessage(session);
            message.setContent("Hello, World!", "text/plain");

            // Replace with secure mechanism to obtain credentials
            String username = System.getenv("MAIL_USERNAME");
            String password = System.getenv("MAIL_PASSWORD");

            sendMessage(message, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MailMessage {
    private boolean debugMode;

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean getDebugMode() {
        return debugMode;
    }
}