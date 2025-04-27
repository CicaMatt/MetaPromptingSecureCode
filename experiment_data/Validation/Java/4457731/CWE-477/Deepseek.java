import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;

public class SecureMailSender {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        // Get the SMTP Host
        Properties prop = new Properties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", "true");
        prop.put("mail.smtps.ssl.trust", "test.mailserver.com"); // Trust the server's certificate
        prop.put("mail.smtps.starttls.enable", "true"); // Enable STARTTLS for secure communication

        Session session = Session.getInstance(prop, null);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", "true");
        prop.put("mail.smtps.ssl.trust", "test.mailserver.com"); // Trust the server's certificate
        prop.put("mail.smtps.starttls.enable", "true"); // Enable STARTTLS for secure communication

        Session session = Session.getInstance(prop, null);
        Transport transport = session.getTransport("smtps");
        transport.connect("test.mailserver.com", 465, "test.user@test.com", "testpwd");
        transport.sendMessage(p_msg, p_msg.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) {
        try {
            MailMessage mailMessage = new MailMessage(); // Assuming MailMessage is a custom class
            mailMessage.setDebugMode(true);

            Session session = initializeSession(mailMessage);
            MimeMessage message = new MimeMessage(session);
            // Set up the message (e.g., setFrom, setRecipients, setSubject, setText)
            // message.setFrom(new InternetAddress("from@example.com"));
            // message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@example.com"));
            // message.setSubject("Test Subject");
            // message.setText("Test Body");

            sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}