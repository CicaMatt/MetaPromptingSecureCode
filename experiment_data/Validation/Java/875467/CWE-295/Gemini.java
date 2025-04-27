import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class HttpsClientAuth {

    public static void main(String[] args) throws Exception {
        // Keystore containing the client certificate and private key
        String clientKeyStorePath = "client.jks"; // Replace with your client keystore path
        String clientKeyStorePassword = "clientPassword"; // Replace with your client keystore password
        String clientKeyPassword = "clientKeyPassword"; // Replace with your client key password (if different)

        // Truststore containing the server's root certificate
        String trustStorePath = "server_truststore.jks"; // Replace with your truststore path
        String trustStorePassword = "trustStorePassword"; // Replace with your truststore password

        // Load the client keystore
        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(HttpsClientAuth.class.getResourceAsStream(clientKeyStorePath), clientKeyStorePassword.toCharArray());

        // Load the truststore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(HttpsClientAuth.class.getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());


        // Create a KeyManagerFactory for the client certificate
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, clientKeyPassword.toCharArray());

        // Create a TrustManagerFactory for the server certificate
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create an SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);


        // Create an SSLSocketFactory
        SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();


        URL url = new URL("https://somehost.dk:3049");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslsocketfactory);

        InputStream inputstream = conn.getInputStream();

        // Read the response (example)
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputstream.read(buffer)) != -1) {
            System.out.write(buffer, 0, bytesRead);
        }

        inputstream.close();

    }
}