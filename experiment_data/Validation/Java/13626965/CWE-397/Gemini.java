import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class FixedCategoryYank {

    // Method demonstrating proper exception handling
    public static String getPV(String urlString) throws IOException { 
        URL url;
        HttpURLConnection conn = null;
        BufferedReader br = null;

        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            if(conn instanceof HttpsURLConnection) {
            	trustAllCerts((HttpsURLConnection) conn);
            }

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.flush();
            wr.close();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
            return null; // Or throw a custom exception
        }  finally {  // Ensure resources are closed even if an exception occurs
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    System.err.println("Error closing BufferedReader: " + ex.getMessage());
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    public static void main(String[] args) {
        String url = "https://www.abc.com"; // Replace with your URL
        try {
            String result = getPV(url);
            if (result != null) {
                System.out.println(result);
            }
        } catch (IOException e) {
            System.err.println("Error making HTTP request: " + e.getMessage());
            e.printStackTrace(); // For detailed debugging information
        }
    }




    private static void trustAllCerts(HttpsURLConnection conn) throws NoSuchAlgorithmException, KeyManagementException{
		TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { 
						return null; 
					} 
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
					} 
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
					}
				} 
		}; 

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL"); 
			sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			conn.setSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			//Do nothing
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) { 
				return true; 
			} 
		}; 

		// Install the all-trusting host verifier
		conn.setHostnameVerifier(allHostsValid); 
	}
}