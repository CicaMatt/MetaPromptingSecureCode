import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SecureProcessor implements Runnable {
    private CountDownLatch latch;
    private String username;
    private String password;

    public SecureProcessor(CountDownLatch latch, String username, String password) {
        this.latch = latch;
        this.username = username;
        this.password = password;
    }

    public void run() {
        System.out.println("Thread started for user: " + username);

        // Simulate some processing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Simulate secure authentication
        if (authenticate(username, password)) {
            System.out.println("Authentication successful for user: " + username);
        } else {
            System.out.println("Authentication failed for user: " + username);
        }

        latch.countDown();
    }

    private boolean authenticate(String username, String password) {
        // Retrieve stored credentials from environment variables
        String storedUsername = System.getenv("SECURE_USERNAME");
        String storedPassword = System.getenv("SECURE_PASSWORD");

        // Compare credentials
        return username.equals(storedUsername) && password.equals(storedPassword);
    }
}

public class SecureApp {

    public static void main(String[] args) {
        // Retrieve credentials from environment variables
        String username = System.getenv("SECURE_USERNAME");
        String password = System.getenv("SECURE_PASSWORD");

        if (username == null || password == null) {
            System.err.println("Credentials not found in environment variables.");
            return;
        }

        CountDownLatch latch = new CountDownLatch(3); // Countdown from 3 to 0

        ExecutorService executor = Executors.newFixedThreadPool(3); // 3 Threads in pool

        for (int i = 0; i < 3; i++) {
            executor.submit(new SecureProcessor(latch, username, password)); // Ref to latch. Each time call new SecureProcessor, latch will count down by 1
        }

        try {
            latch.await();  // Wait until latch counted down to 0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All threads completed.");
    }
}