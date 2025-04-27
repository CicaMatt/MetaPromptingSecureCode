import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Processor implements Runnable {
    private CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");

        try {
            Thread.sleep(3000); // Simulate some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.countDown(); // Decrement the latch count
        System.out.println("Latch count: " + latch.getCount());
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3); // Initialize with count 3

        ExecutorService executor = Executors.newFixedThreadPool(3); // Create a thread pool with 3 threads

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch)); // Submit tasks to the executor
        }

        try {
            latch.await(); // Wait until the latch count reaches zero
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Completed.");

        executor.shutdown(); // Shutdown the executor
    }
}