import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureMailSender {

    private static final Logger LOGGER = Logger.getLogger(SecureMailSender.class.getName());
    private static final String CONFIG_FILE = "mail.properties"; // Configuration file

    public static void sendMail(MimeMessage p_msg) throws MessagingException, IOException, GeneralSecurityException {
        Properties mailProps = loadProperties(CONFIG_FILE);

        String host = mailProps.getProperty("mail.smtps.host");
        int port = Integer.parseInt(mailProps.getProperty("mail.smtps.port", "465")); // Default to 465
        String username = mailProps.getProperty("mail.user");
        String password = mailProps.getProperty("mail.password");
        boolean debug = Boolean.parseBoolean(mailProps.getProperty("mail.debug", "false"));

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Use STARTTLS for enhanced security
        props.put("mail.smtp.ssl.trust", host);          // Trust the specific host's certificate
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Explicitly set SSLSocketFactory
        props.put("mail.smtp.socketFactory.fallback", "false");                      // Disable fallback to plain text

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        session.setDebug(debug);

        try (Transport transport = session.getTransport("smtps")) {
            transport.connect(host, port, username, password);
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error sending email:", e);
            throw e; // Re-throw MessagingException for handling by caller
        }
    }



    private static Properties loadProperties(String configFile) throws IOException {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            props.load(input);
        }
        return props;
    }



    public static void main(String[] args) throws MessagingException, GeneralSecurityException, IOException {
        // Example usage (replace with your MimeMessage creation)
        // ... create MimeMessage ...

        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties())); // Replace with your message creation
        message.setFrom("your_email@example.com");
        message.setRecipients(Message.RecipientType.TO, "recipient_email@example.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");


        sendMail(message);

        System.out.println("Email sent successfully!");
    }
}