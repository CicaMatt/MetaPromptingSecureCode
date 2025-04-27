import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.net.ssl.SSLHandshakeException;

public class MailSender {

    protected static Session initializeSession(MailMessage p_msg) throws MessagingException { // Specific Exception

        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);
        prop.put("mail.smtps.port", "465"); // Explicitly set the port
        prop.put("mail.smtps.socketFactory.port", "465"); // Important for SSL
        prop.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtps.socketFactory.fallback", "false"); // Disable fallback to plain


        Session session = Session.getInstance(prop, null);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws MessagingException { // Specific Exception

        try {
            Session session = initializeSession(p_msg); // Use the initialized session
            Transport transport = session.getTransport("smtps");


            transport.connect("test.mailserver.com", 465, "test.user@test.com", "testpwd");
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
            transport.close();

        } catch (MessagingException mex) {
            // Handle MessagingException (and its subtypes like AuthenticationFailedException)
            System.err.println("MessagingException: " + mex.getMessage());
            if (mex instanceof AuthenticationFailedException) {
                System.err.println("Authentication failed. Check credentials.");

            } else if (mex.getNextException() instanceof SSLHandshakeException){
                System.err.println("SSL Handshake Exception: " + mex.getNextException().getMessage());
                // Handle certificate issues here (see explanations below)
            }
            throw mex; // Re-throw after logging/handling – allows the caller to handle it further if needed.

        }
    }

    public static void main(String[] args) {  // Example usage (replace with your actual message creation)
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            // ... set message content ...

            sendMessage(message);
            System.out.println("Message sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging.
        }

    }
}