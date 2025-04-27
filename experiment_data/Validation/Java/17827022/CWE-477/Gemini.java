import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Processor implements Runnable {
    private CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Started.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " finished its task.");
        latch.countDown(); // Decrement the latch count
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3); // Initialize the latch with a count of 3

        ExecutorService executor = Executors.newFixedThreadPool(3); // Create a thread pool with 3 threads

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch)); // Submit 3 Processor tasks to the thread pool
        }

        System.out.println("Main thread waiting for all tasks to complete...");

        try {
            latch.await(); // Main thread blocks here until the latch count becomes 0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All tasks completed. Main thread continuing.");
        executor.shutdown(); // Shut down the executor to prevent resource leaks
    }
}