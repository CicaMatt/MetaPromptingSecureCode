import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Processor implements Runnable {
    private CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // More specific handling. Log and interrupt current thread 
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
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
            // Wait with a timeout and handle the TimeoutException
            if (!latch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Latch timed out!");
                // Perform timeout handling (e.g., shutdown executor)
                executor.shutdownNow(); // Attempt immediate shutdown
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            System.err.println("Main thread interrupted: " + e.getMessage());
        } finally {
            // Ensure proper shutdown of the executor
            if (!executor.isShutdown()) {
                executor.shutdown(); // Initiate orderly shutdown
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("Executor did not terminate in time.");
                        executor.shutdownNow(); // Forceful shutdown
                    }
                } catch (InterruptedException ie) {
                    System.err.println("Interrupted while waiting for executor shutdown.");
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println("Completed.");
    }
}