import java.security.Security;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SecureEmail {

    protected static Session initializeSession(MailMessage p_msg) throws Exception {

        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", "test.mailserver.com");
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);
        prop.put("mail.smtps.port", "465"); // Explicitly set the port
        prop.put("mail.smtps.socketFactory.port", "465"); // Important for SSL
        prop.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Important for SSL
        prop.put("mail.smtps.socketFactory.fallback", "false"); // Don't fall back to plain


        // START: Handling Self-Signed or Untrusted Certificates (ONLY for testing/development)
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            Security.setProperty("ssl.SocketFactory.provider", "sun.security.ssl.SSLSocketFactoryImpl"); // Some JVMs need this
            prop.put("mail.smtps.ssl.socketFactory", sc.getSocketFactory());
            prop.put("mail.smtps.ssl.checkserveridentity", "false"); // Disable host verification
            

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SSL context", e);
        }

        // END: Handling Self-Signed Certificates


        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test.user@test.com", "testpwd"); // Replace with your credentials
            }
        });

        session.setDebug(p_msg.getDebugMode());
        return session;
    }

    protected static void sendMessage(MimeMessage p_msg) throws Exception {
        Session session = initializeSession(p_msg);  // Reuse session
        Transport transport = session.getTransport("smtps");

        transport.connect();  // No need to specify parameters again, they are in the session
        transport.sendMessage(p_msg, p_msg.getAllRecipients());
        transport.close();
    }



    public static void main(String[] args) throws Exception {
        // Example usage (replace with your actual MimeMessage creation)
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        // ... set message properties (to, from, subject, body, etc.) ...

        // Set debug mode to true for debugging
        // message.setDebugMode(true); 

        sendMessage(message);
        System.out.println("Message sent successfully!");


    }

}