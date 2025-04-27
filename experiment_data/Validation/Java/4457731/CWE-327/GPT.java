import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.security.Security;

public class SecureMail {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        // Set the SMTP host properties, alongside enabling SSL/TLS
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "test.mailserver.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        prop.put("mail.smtp.ssl.checkserveridentity", true);

        // Enable strict validation of server certificates
        Security.setProperty("ssl.SocketFactory.provider", "com.sun.net.ssl.internal.ssl.SSLSocketFactoryImpl");

        // Obtain the session object with a custom Authenticator if needed
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
        Properties prop = new Properties();
        Session session = initializeSession(p_msg);

        try (Transport transport = session.getTransport("smtps")) {
            transport.connect();
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        }
    }

    public static void main(String[] args) throws Exception {
        // Example usage
        MailMessage msg = new MailMessage();
        msg.setDebugMode(true);

        MimeMessage mimeMessage = new MimeMessage(initializeSession(msg));
        mimeMessage.setFrom(new InternetAddress("test.user@test.com"));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("recipient@example.com"));
        mimeMessage.setSubject("Test Email");
        mimeMessage.setText("This is a test email.");

        sendMessage(mimeMessage);
    }
}

class MailMessage {
    private boolean debugMode;

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}