import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SecureEmailSender {

    private static final String CONFIG_FILE = "email.properties"; // Path to your configuration file

    public static void main(String[] args) throws Exception {
        // Load credentials from configuration file
        Properties config = new Properties();
        try {
            config.load(SecureEmailSender.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            System.exit(1); // Terminate if config loading fails
        }

        String username = config.getProperty("mail.user");
        String password = config.getProperty("mail.password");
        String host = config.getProperty("mail.host");
        int port = Integer.parseInt(config.getProperty("mail.port", "465")); // Default to 465 if not specified


        // Example usage
        MailMessage mail = new MailMessage();
        mail.setDebugMode(true); // Set to false in production

        MimeMessage message = createMessage(mail, username, host);

        sendMessage(message, host, port, username, password);
    }



    protected static Session initializeSession(String username, String password, String host, int port)  {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // or use smtps and remove this line if server supports it directly
        prop.put("mail.smtp.ssl.enable", "true"); // Required for secure connection

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }


    protected static MimeMessage createMessage(MailMessage mail, String from, String host) throws Exception {
        Session session = initializeSession(from, "", host, 0); // Password not needed here

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo()));
        message.setSubject(mail.getSubject());
        message.setText(mail.getBody());

        return message;
    }

    protected static void sendMessage(MimeMessage p_msg, String host, int port, String username, String password) throws Exception {
        Session session = initializeSession(username, password, host, port);
        Transport transport = session.getTransport("smtp");
        transport.connect(host, port, username, password);
        transport.sendMessage(p_msg, p_msg.getAllRecipients());
        transport.close();
    }


    // Simple MailMessage class (replace with your existing one if you have one)
    static class MailMessage {
        private boolean debugMode;
        private String to;
        private String subject;
        private String body;


        // Getters and setters ...
        public boolean getDebugMode() { return debugMode; }
        public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }

}