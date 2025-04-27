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
            Thread.currentThread().interrupt(); // Proper thread interruption handling
            e.printStackTrace();
        }

        latch.countDown(); // Decrement the count of the latch
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3); // Count down from 3 to 0

        ExecutorService executor = Executors.newFixedThreadPool(3); // 3 Threads in pool

        for(int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            latch.await();  // Wait until latch counted down to 0
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Proper thread interruption handling
            e.printStackTrace();
        }

        System.out.println("Completed.");
        
        executor.shutdown(); // Ensure proper shutdown of the executor service
    }
}