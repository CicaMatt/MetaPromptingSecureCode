import java.security.Security;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;

public class SecureMailSender {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        // Get the SMTP Host
        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", "true");
        prop.put("mail.smtps.port", "465"); // Explicitly set the port

        // Add properties to mitigate issues with older TLS versions if needed
        // These lines should be included especially if the server supports newer TLS versions
        // and the client environment has older, insecure protocols enabled by default.
        Security.setProperty("jdk.tls.disabledAlgorithms", "SSLv3, TLSv1, TLSv1.1"); // Disable older, insecure TLS versions
        prop.put("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3"); // Enable TLS 1.2 and TLS 1.3 (adjust as needed)
        prop.put("mail.smtps.ssl.trust", "*"); // ONLY for testing if certificate is self-signed and you understand the risk. REMOVE in production!


        // Create the session using a custom authenticator to encapsulate credentials
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test.user@test.com", "testpwd");
            }
        });

        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws Exception {
        Session session = initializeSession(p_msg); // Reuse initialized session


        try (Transport transport = session.getTransport("smtps")) {
            transport.connect(); // Use the implicit connection with credentials from the session
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        }

    }



    public static void main(String[] args) throws Exception {
        // Example usage (replace with your actual message creation logic)
        Properties props = new Properties(); // or reuse session properties

        Session sessionForMessage = Session.getInstance(props); // Session just for message creation
        MimeMessage message = new MimeMessage(sessionForMessage );
        message.setFrom("test.user@test.com");
        message.setRecipients(Message.RecipientType.TO, "recipient@example.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");
        message.setDebug(true);


        sendMessage(message);

        System.out.println("Message sent successfully!");
    }
}