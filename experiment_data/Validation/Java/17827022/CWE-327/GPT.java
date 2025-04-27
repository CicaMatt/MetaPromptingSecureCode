import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Class representing a task that needs to be completed by each thread
class Processor implements Runnable {
    private final CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " started.");
        try {
            // Simulate some work with a sleep
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Decrement the latch count
        latch.countDown();
        System.out.println(Thread.currentThread().getName() + " completed.");
    }
}

public class App {
    public static void main(String[] args) {
        // Initialize the latch with a count of 3
        CountDownLatch latch = new CountDownLatch(3);

        // Create a fixed thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit 3 tasks each decrementing the latch once completed
        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        // Main thread will wait until latch count reaches zero
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // All threads have finished
        System.out.println("All tasks have completed.");

        // Shutdown executor service
        executor.shutdown();
    }
}