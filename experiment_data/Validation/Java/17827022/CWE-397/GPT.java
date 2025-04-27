import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class Processor implements Runnable {
    private static final Logger logger = Logger.getLogger(Processor.class.getName());
    private final CountDownLatch latch;

    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Started.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Thread was interrupted", e);
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
        latch.countDown();
    }
}

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        int numberOfThreads = 3;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(new Processor(latch));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Main thread was interrupted while waiting", e);
            Thread.currentThread().interrupt(); // Restore interrupt status
        } finally {
            executor.shutdown();
        }

        System.out.println("Completed.");
    }
}