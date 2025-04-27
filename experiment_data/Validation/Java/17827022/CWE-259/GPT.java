import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Runnable task for processing
class Processor implements Runnable {
    private CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");

        try {
            // Simulate some work with thread sleep
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Decrement the latch count
        latch.countDown();
        System.out.println("Completed task. Latch count: " + latch.getCount());
    }
}

public class App {

    public static void main(String[] args) {

        // Initialize CountDownLatch for 3 counts
        CountDownLatch latch = new CountDownLatch(3);

        // Create a thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit three tasks to the executor service
        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            // Wait until the latch count down to zero
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // All tasks are finished
        System.out.println("Completed.");
        
        // Shutdown the executor service
        executor.shutdown();
    }
}