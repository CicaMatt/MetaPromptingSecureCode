import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Processor implements Runnable {
    private CountDownLatch latch;
    private int id;

    public Processor(CountDownLatch latch, int id) {
        this.latch = latch;
        this.id = id;
    }

    public void run() {
        System.out.println("Thread " + id + " Started.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread " + id + " finished its work.");
        latch.countDown(); // Decrement the latch count
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3); // Initialize latch to 3

        ExecutorService executor = Executors.newFixedThreadPool(3); // Create a thread pool with 3 threads

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch, i)); // Submit 3 tasks to the thread pool
        }

        System.out.println("Main thread awaiting latch.");
        try {
            latch.await(); // Main thread waits until latch counts down to 0
            System.out.println("All threads have completed their work.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Completed."); // Executed after all threads have decremented the latch
        executor.shutdown(); // Cleanly shut down the thread pool

    }
}