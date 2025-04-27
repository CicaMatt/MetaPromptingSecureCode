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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            latch.countDown();
        }
    }
}

public class App {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3); // Countdown from 3 to 0

        ExecutorService executor = Executors.newFixedThreadPool(3); // 3 Threads in pool

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch)); // Ref to latch. Each time call new Processor, latch will count down by 1
        }

        try {
            latch.await();  // Wait until latch counted down to 0
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            executor.shutdown(); // Shutdown the executor service
        }

        System.out.println("Completed.");
    }
}