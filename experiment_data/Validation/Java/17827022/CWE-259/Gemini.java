import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Processor implements Runnable {
    private CountDownLatch latch;
    private int id; // Added ID to identify threads

    public Processor(CountDownLatch latch, int id) {
        this.latch = latch;
        this.id = id;
    }

    public void run() {
        System.out.println("Thread " + id + " started.");

        try {
            // Simulate work taking a random amount of time
            Thread.sleep((long) (Math.random() * 2000)); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread " + id + " finished its work.");
        latch.countDown(); // Signal completion
    }
}

public class App {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(3); 

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch, i)); 
        }

        try {
            System.out.println("Main thread waiting for worker threads to finish...");
            boolean finishedOnTime = latch.await(5, TimeUnit.SECONDS); // Wait with a timeout
            if (finishedOnTime) {
                System.out.println("All worker threads completed.");
            } else {
                System.out.println("Timeout occurred. Some worker threads might not have finished.");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown(); // Important: Shutdown the executor
    }
}