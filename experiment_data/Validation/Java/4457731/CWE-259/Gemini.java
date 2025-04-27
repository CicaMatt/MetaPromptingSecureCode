import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SecureMailSender {

    private static final String SMTP_HOST_KEY = "mail.smtps.host";
    private static final String SMTP_PORT_KEY = "mail.smtps.port";
    private static final String SMTP_USER_KEY = "mail.smtps.user";
    private static final String SMTP_PASSWORD_KEY = "mail.smtps.password";


    protected static Session initializeSession() throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.transport.protocol", "smtps");
        prop.put("mail.smtps.auth", true);
        // Port is optional, defaults to 465 for smtps
        prop.put(SMTP_PORT_KEY, "465");

        // Fetch credentials from environment variables
        String smtpHost = System.getenv(SMTP_HOST_KEY);
        String smtpUser = System.getenv(SMTP_USER_KEY);
        String smtpPassword = System.getenv(SMTP_PASSWORD_KEY);

        // OR fetch from a properties file (See commented-out example below)
       // loadPropertiesFromFile(prop, "email.properties");


        // Fail if essential properties are missing
        if (smtpHost == null || smtpUser == null || smtpPassword == null) {
            throw new MessagingException("Missing SMTP configuration. Please set environment variables or properties file.");
        }

        prop.put(SMTP_HOST_KEY, smtpHost);


        // Create a session with authentication
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        return session;

    }

    // Example of loading from properties file (Uncomment if needed)
//    private static void loadPropertiesFromFile(Properties prop, String filename) throws IOException {
//        try (InputStream input = new FileInputStream(filename)) {
//            prop.load(input);
//        }
//    }

    protected static void sendMessage(MimeMessage p_msg, boolean debugMode) throws MessagingException {
        Session session = initializeSession();
        session.setDebug(debugMode);

        try (Transport transport = session.getTransport("smtps")) { // Use try-with-resources for automatic closing

            // Connect using the credentials provided in the session creation
            transport.connect(); // Host, port, user, and password are handled by the Authenticator

            transport.sendMessage(p_msg, p_msg.getAllRecipients());
        }
    }



    public static void main(String[] args) throws Exception {
        // Example usage (replace with your message creation logic):

        Properties props = new Properties();
        Session session = Session.getInstance(props);

        MimeMessage message = new MimeMessage(session);
        // ... set message properties (to, from, subject, body, etc.) ...

        sendMessage(message, true); // Set debugMode to true for debugging information
        System.out.println("Message sent successfully!");

    }

}