import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Processor implements Runnable {
    private final CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Handle InterruptedException more specifically if needed.  Log the error.
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Thread interrupted: " + e.getMessage());
            return; // Exit the run method if interrupted
        }

        latch.countDown();
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            // Use await with a timeout to prevent indefinite blocking
            if (!latch.await(10, TimeUnit.SECONDS)) {  // Example timeout of 10 seconds
                System.err.println("Timeout waiting for latch. Some threads might not have finished.");
                executor.shutdownNow(); // Attempt to stop any remaining tasks
            }
        } catch (InterruptedException e) {
            // Restore interrupted status and handle the exception appropriately
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted: " + e.getMessage());
            executor.shutdownNow(); // Attempt to stop any remaining tasks
            return; // Exit main if interrupted
        } finally {
            // Ensure proper shutdown of the executor
            executor.shutdown();
        }



        System.out.println("Completed.");
    }
}