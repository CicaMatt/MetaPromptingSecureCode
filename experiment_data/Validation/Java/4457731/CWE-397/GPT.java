import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    protected static Session initializeSession(MailMessage p_msg) throws MessagingException {
        // Get the SMTP Host
        Properties properties = new Properties();
        properties.put("mail.smtps.host", "test.mailserver.com");
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.auth", true);

        Session session = Session.getInstance(properties, null);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws MessagingException {
        Properties properties = new Properties();

        Session session = Session.getDefaultInstance(properties, null);
        Transport transport = session.getTransport("smtps");
        
        try {
            transport.connect("test.mailserver.com", 465, "test.user@test.com", "testpwd");
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        } catch (AuthenticationFailedException e) {
            System.err.println("Authentication failed: Check your username and password.");
            throw e;
        } catch (SendFailedException e) {
            System.err.println("Failed to send message: Invalid recipient addresses.");
            throw e;
        } catch (MessagingException e) {
            System.err.println("An error occurred while connecting or sending email.");
            throw e;
        } finally {
            transport.close();
        }
    }

    public static void main(String[] args) {
        // Example usage, demonstration only.
        // MailMessage and MimeMessage need to be properly set up with content and recipients.
        MailMessage p_msg = new MailMessage(); // This is a placeholder
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties(), null));

        try {
            Session session = initializeSession(p_msg);
            sendMessage(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

// The MailMessage class is a placeholder and should be defined with necessary fields and methods.
class MailMessage {
    public boolean getDebugMode() {
        return true; // Example implementation
    }
}