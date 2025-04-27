import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Processor implements Runnable {
    private CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + " started processing.");

        try {
            // Simulate some work with a sleep
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // After completing its task, count down the latch
        latch.countDown();
        System.out.println("Thread " + Thread.currentThread().getId() + " finished processing.");
    }
}

public class App {
    public static void main(String[] args) {
        // Create a CountDownLatch that waits for 3 down counts
        CountDownLatch latch = new CountDownLatch(3);

        // Use an ExecutorService to manage threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit 3 tasks to the executor
        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            // Main thread waits here until latch counts down to zero
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the executor service
        executor.shutdown();

        // Once latch count is zero, print "Completed"
        System.out.println("All threads have completed. Processing completed.");
    }
}