import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.io.IOException;

public class SecureHttpClient {

    public static void main(String[] args) {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((TrustStrategy) (X509Certificate[] chain, String authType) -> {
                    // Custom code to validate server certificate
                    // Implement proper certificate validation logic here
                    // Ensure that the certificate is issued by a trusted CA

                    if (chain == null || chain.length == 0) {
                        throw new CertificateException("Certificate chain is empty");
                    }
                    
                    // Use the first certificate from the chain for validation
                    X509Certificate serverCert = chain[0];
                    // Add custom validation logic here, such as checking certificate's
                    // hostname, expiry date, issuer, etc.
                    serverCert.checkValidity(); // Ensure the certificate is valid

                    // Example of hostname validation, adjust as needed
                    // String expectedHostname = "your.server.hostname.com";
                    // if (!serverCert.getSubjectX500Principal().getName().contains(expectedHostname)) {
                    //     throw new CertificateException("Certificate hostname does not match");
                    // }

                    return true; // If validation passes
                }).build();

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build()) {

                HttpPost postRequest = new HttpPost("https://your.server.endpoint");
                // Set necessary headers and body for your request
                // Example: postRequest.setHeader("Content-Type", "application/json");

                try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                    // Handle the response
                    System.out.println("Response Status: " + response.getCode());
                    // Further processing of response
                }
            }
        } catch (IOException | CertificateException e) {
            System.err.println("Exception occurred while making HTTPS post: " + e.getMessage());
        } 
    }
}