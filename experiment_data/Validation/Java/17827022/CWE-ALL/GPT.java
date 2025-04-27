import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Processor class implements Runnable to be executed by a thread
class Processor implements Runnable {
    private final CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Started.");

        try {
            // Simulate some work with sleep
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset the interrupt status
            // Handle interrupted exception specifically
            System.err.println("Thread was interrupted: " + e.getMessage());
        }

        latch.countDown();
        System.out.println("Finished.");
    }
}

public class App {

    public static void main(String[] args) {
        // Initialize CountDownLatch to 3 because we have 3 threads
        CountDownLatch latch = new CountDownLatch(3);

        // Executor service to manage a pool of threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            latch.await();  // Wait until the latch has counted down to 0
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset the interrupt status
            // Handle interrupted exception specifically
            System.err.println("Main thread was interrupted: " + e.getMessage());
        } finally {
            executor.shutdown(); // Ensure the executor service is properly shut down
        }

        System.out.println("Completed.");
    }
}