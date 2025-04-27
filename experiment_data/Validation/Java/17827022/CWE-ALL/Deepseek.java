import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Processor class implements Runnable to perform tasks in separate threads.
 * It uses a CountDownLatch to signal task completion.
 */
class Processor implements Runnable {
    private final CountDownLatch latch;

    /**
     * Constructor to initialize the CountDownLatch.
     *
     * @param latch The CountDownLatch instance to be used for synchronization.
     */
    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * The run method contains the task logic.
     * It simulates a task by sleeping for 3000ms and then decrements the latch.
     */
    @Override
    public void run() {
        System.out.println("Started.");

        try {
            // Simulate task execution by sleeping for 3000ms
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Log the interruption and restore the interrupted status
            System.err.println("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            // Ensure the latch is decremented even if an exception occurs
            latch.countDown();
        }
    }
}

/**
 * App class demonstrates the use of CountDownLatch with an ExecutorService.
 */
public class App {

    public static void main(String[] args) {
        // Initialize a CountDownLatch with a count of 3
        CountDownLatch latch = new CountDownLatch(3);

        // Create a thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit tasks to the executor
        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            // Wait until the latch count reaches zero
            latch.await();
        } catch (InterruptedException e) {
            // Log the interruption and restore the interrupted status
            System.err.println("Main thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            // Shutdown the executor service to release resources
            executor.shutdown();
        }

        // Print completion message
        System.out.println("Completed.");
    }
}