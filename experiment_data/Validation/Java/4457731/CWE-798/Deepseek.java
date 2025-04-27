import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SecureMailSender {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {
        // Get the SMTP Host from environment variables
        String smtpHost = System.getenv("SMTP_HOST");
        String smtpPort = System.getenv("SMTP_PORT");
        String smtpUser = System.getenv("SMTP_USER");
        String smtpPassword = System.getenv("SMTP_PASSWORD");

        // Configure SSL context to trust all certificates (for testing purposes only)
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }}, new java.security.SecureRandom());
        SSLContext.setDefault(sslContext);

        // Set mail properties
        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", smtpHost);
        prop.put("mail.smtps.port", smtpPort);
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);
        prop.put("mail.smtps.ssl.enable", true);
        prop.put("mail.smtps.ssl.trust", "*");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws Exception {
        Session session = initializeSession(new MailMessage());
        Transport transport = session.getTransport("smtps");
        transport.connect();
        transport.sendMessage(p_msg, p_msg.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) {
        try {
            // Example usage
            Session session = initializeSession(new MailMessage());
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@example.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("to@example.com"));
            message.setSubject("Test Subject");
            message.setText("Test Body");

            sendMessage(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MailMessage {
    private boolean debugMode = true;

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}