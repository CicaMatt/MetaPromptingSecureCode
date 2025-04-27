import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SecureMailSender {

    private static final String CONFIG_FILE = "mail_config.properties";

    protected static Session initializeSession(boolean debugMode) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtps.host", getProperty("mail.smtps.host"));
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);

        // SSL settings
        prop.put("mail.smtps.ssl.enable", "true");
        prop.put("mail.smtps.ssl.trust", getProperty("mail.smtps.host"));

        Session session = Session.getInstance(prop, null);
        session.setDebug(debugMode);
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws Exception {
        Session session = initializeSession(true);
        Transport transport = session.getTransport("smtps");

        String host = getProperty("mail.smtps.host");
        int port = Integer.parseInt(getProperty("mail.smtps.port"));
        String user = getProperty("mail.user");
        String password = getProperty("mail.password");

        transport.connect(host, port, user, password);
        transport.sendMessage(p_msg, p_msg.getAllRecipients());
        transport.close();
    }

    private static String getProperty(String key) throws IOException {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            prop.load(input);
        }
        return prop.getProperty(key);
    }

    public static void main(String[] args) {
        try {
            Session session = initializeSession(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("test.user@test.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("recipient@example.com"));
            message.setSubject("Secure Email");
            message.setText("This is a secure email sent without hard-coded credentials.");

            sendMessage(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}