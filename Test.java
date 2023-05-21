import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {

    // verbose mode
    static final boolean verbose = true;

    // port to listen for connection
    static final int PORT = 8085;

    // connection ID
    static int count = 0;

    public static void main(String[] args) {

        // create a thread-pool consisting of 5 worker threads
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // create a FIFO queue of size 15 to store incoming requests
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(15);

        try {
            // create a server listening socket
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // listen until user halts server execution
            while (true) {
                // accept client connection request
                Socket connect = serverConnect.accept();
                count++;

                if (verbose) {
                    System.out.println("Connection " + count + " opened. (" + new Date() + ")");
                }

                // create a task to manage the client connection
                final int connectionId = count;
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        ServeWebRequest s = new ServeWebRequest();
                        s.serve(connect, connectionId);
                    }
                };

                // add the task to the queue
                queue.put(task);

                // submit the next task from the queue to the thread pool
                executor.execute(queue.take());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
}
