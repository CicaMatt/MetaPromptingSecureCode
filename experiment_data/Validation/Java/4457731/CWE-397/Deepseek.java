import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.security.Security;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class MailSender {

    protected static Session initializeSession(MailMessage p_msg) throws MessagingException {
        // Get the SMTP Host
        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);

        Session session = Session.getInstance(prop, null);
        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws MessagingException {
        Properties prop = System.getProperties();

        // Configure SSL context to trust all certificates (for testing purposes only)
        try {
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
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            prop.put("mail.smtps.socketFactory", sslSocketFactory);
            prop.put("mail.smtps.socketFactory.port", "465");
        } catch (Exception e) {
            throw new MessagingException("Failed to configure SSL context", e);
        }

        Session session = Session.getDefaultInstance(prop, null);
        Transport transport = session.getTransport("smtps");
        try {
            transport.connect("test.mailserver.com", 465, "test.user@test.com", "testpwd");
            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    public static void main(String[] args) {
        try {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setDebugMode(true);

            Session session = initializeSession(mailMessage);
            MimeMessage mimeMessage = new MimeMessage(session);
            // Set up the MimeMessage (e.g., setFrom, setRecipients, setSubject, setText)

            sendMessage(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle specific messaging exceptions
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions
        }
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