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
        System.out.println(Thread.currentThread().getName() + " Started."); // Identify the thread

        try {
            Thread.sleep(3000);  // Simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption properly
            System.err.println(Thread.currentThread().getName() + " interrupted.");
        }

        latch.countDown();
        System.out.println(Thread.currentThread().getName() + " finished and decremented latch.");
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
            // Added timeout for await to demonstrate handling this case
            if (!latch.await(10, TimeUnit.SECONDS)) {  // Wait with a timeout
                System.out.println("Latch timed out. Some threads might not have finished.");
            } else {
                System.out.println("Completed successfully. All threads finished within timeout.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted while waiting for latch.");

        } finally {
            executor.shutdown(); // Cleanly shut down the executor
        }
    }
}