import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SecureMailSender {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "test.mailserver.com");
        prop.put("mail.smtp.port", "465"); // or 587
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465"); // or 587
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.starttls.enable", "true"); // For port 587


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
        Session session = initializeSession(p_msg); // Reuse the correctly configured session

        Transport.send(p_msg); // Simpler and preferred way to send

    }

    public static void main(String[] args) throws Exception {
        // Example usage (replace with your actual message creation)
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom("test.user@test.com");
        message.setRecipients(Message.RecipientType.TO, "recipient@example.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");
        message.setDebug(true);


        sendMessage(message);
        System.out.println("Message sent successfully");

    }
}