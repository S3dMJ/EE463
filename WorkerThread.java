import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * WorkerThread class represents a worker thread that handles incoming requests
 * from the shared scheduling list of buffers.
 *
 * @author Khaled M. Al-Dahasy [1935129]
 * @version 1.3 2023/6/5
 */

public class WorkerThread implements Runnable {
    private static Queue schedulingList;
    private Socket socket;
    private QueueElement element;
    private int count;
    private static Socket connect;
    private boolean isRunning;


    /**
     * Constructs a WorkerThread object.
     *
     * Precondition: None.
     * Postcondition: A WorkerThread object is created with the isRunning flag set to true.
     */
    public WorkerThread() {
        this.isRunning = true;
    }

    @Override
    public void run() {
        // Listen until the user halts server execution
        while (isRunning) {
            try {
                    

                while (true) {
                    try {
                         WebServer.consume.acquire();
                         WebServer.queueProtect.acquire();
                         element = WebServer.queue.deQueue();
                         WebServer.queueProtect.release();
                         WebServer.produce.release();
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                            WebServer.consume.release();
                            continue;
                                                         }
                      if(element==null) {continue;}
                      socket = element.getSocket();
                      count = element.getCount();
                    // Manage the client connection if the socket is open
                      try{
                        WebServer.write.acquire();
                        System.out.println("Connection " + count + " opened (" + new Date() + ")");
                        WebServer.write.release(); }
                       catch (InterruptedException e) {
                                e.printStackTrace();
                            WebServer.write.release();
                                                         }
												       
                        WebServer.serveWebRequest.serve(socket, count);
                    
                }

            } catch (NullPointerException e) {
                // Reset the loop if a NullPointerException is caught
                System.err.println("Caught NullPointerException: " + e.getMessage());
                break;
            } 

        }
    }
    /**
     * Stops the worker thread.
     *
     * Precondition: None.
     * Postcondition: The isRunning flag is set to false, stopping the execution of the worker thread.
     */
    public void stop() {
        isRunning = false;
    }

}
