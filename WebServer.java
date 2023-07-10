import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;
import java.util.Date;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**

The WebServer class represents a basic web server that listens for client connections,
manages a queue of requests, and serves each request using a pool of worker threads.
It provides functionality to handle different overload policies and logs server activities.
Authors:
Adapted from SSaurel's Blog
Dr. Abdulghani M. Al-Qasimi
Modified by BG4
Saad A. Al-Jehani [1935151]
Khaled M. Al-Dahasy [1935129]
Nawaf S. Al-Harbi [1936576]
Version: 2.1
Update Date: 2023/6/5
Precondition: The server is running and configured with the desired port, number of threads,
queue size, and overload handling policy.
Postcondition: The server listens for client connections, manages the request queue,
and serves each request using the worker threads according to the specified overload policy.
The server logs its activities to a file called "server-log".
Inputs: Command line arguments specifying the port number, number of threads, queue size,
and overload handling policy. User prompts to confirm or change the default values.
Outputs: Server activities and status messages displayed on the console and logged in the "server-log" file.
*/

public class WebServer {


//////////////////////////////////////////////// INITIALIZATION ////////////////////////////////////////////////


    // verbose mode
    static boolean verbose = true;

    // default port to listen for connection
    static final int DEFAULT_PORT = 8085;

    // default number of threads
    static final int DEFAULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();

    // default queue size
    static final int DEFAULT_QUEUE_SIZE = 10;

    // default Overload Handling Policy
    static final String DEFAULT_OVERLOAD_POLICY = "BLCK";

    // connection ID
    static int count = 0;
	// The request queue
    static Queue queue;
	// List to hold the worker threads
    static List<Thread> workerThreads = new ArrayList<>();
	// Semaphore for producer-consumer pattern
    static Semaphore produce;
    static Semaphore consume;
	// Semaphore to protect the queue access
    static Semaphore queueProtect = new Semaphore(1, true);
    // Instance of ServeWebRequest for handling web requests
	static ServeWebRequest serveWebRequest;
	// Semaphore to synchronize write access to the log file
    static Semaphore write;
    
//////////////////////////////////////////////// MAIN METHOD ////////////////////////////////////////////////
	/**
 * The main method is the entry point of the WebServer application.
 * It initializes the server parameters, starts worker threads, and accepts client connections.
 * 
 * @param args Command-line arguments (optional): [port] [numThreads] [queueSize] [overloadPolicy]
 */

    public static void main(String[] args) {


//////////////////////////////////////////////// USER PROMPTS ////////////////////////////////////////////////


        // get the port number from the command line arguments or use the default value
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        // get the number of threads from the command line arguments or use the default
        // value
        int numThreads = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_NUM_THREADS;

        // get the queue size from the command line arguments or use the default value
        int queueSize = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_QUEUE_SIZE;

        // get the overload handling policy from the command line arguments or use the
        // default value
        String overloadPolicy = args.length > 3 ? args[3] : DEFAULT_OVERLOAD_POLICY;

        // if all parameters are missing, prompt the user to confirm or change the
        // default values
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Using default values:");
            System.out.println("Port number: " + port);
            System.out.println("Number of threads: " + numThreads);
            System.out.println("Queue size: " + queueSize);
            System.out.println("Overload handling policy: " + overloadPolicy);
            System.out.println("Do you want to change these values? (y/n)");

            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("y")) {
                System.out.print("Enter port number (" + DEFAULT_PORT + "): ");
                port = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter number of threads (" + DEFAULT_NUM_THREADS + "): ");
                numThreads = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter queue size (" + DEFAULT_QUEUE_SIZE + "): ");
                queueSize = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter overload handling policy (BLCK/DRPT/DRPH): ");
                overloadPolicy = scanner.nextLine();
            }
        }

        System.out.println("\nThe server is running..\n\n");


//////////////////////////////////////////////// SERVER ////////////////////////////////////////////////


        try {
            write = new Semaphore(1, true);

            // create a PrintStream that writes to a file called server-log
            PrintStream out = new PrintStream("web-server-log");

            // set System.out to write to the log file
            System.setOut(out);

            // print the port number, number of threads, and queue size to the log file
            write.acquire();
            System.out.println("Port number: " + port);
            System.out.println("Number of threads: " + numThreads);
            System.out.println("Queue size: " + queueSize);
            System.out.println("Overload handling policy: " + overloadPolicy);
            write.release();

            queue = new Queue(queueSize);
            produce = new Semaphore(queueSize, true);
            consume = new Semaphore(0, true);

            // start worker threads
            for (int i = 0; i < numThreads; i++) {
                Thread thread = new Thread(new WorkerThread());
                workerThreads.add(thread);
                thread.start();
            }

            // Create and start the monitor thread
            MonitorThread monitorThread = new MonitorThread();
            Thread monitorThreadThread = new Thread(monitorThread);
            monitorThreadThread.start();

            try {
                // create a server listening socket
                ServerSocket serverConnect = new ServerSocket(port);
                write.acquire();
                System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");
                write.release();
                serveWebRequest = new ServeWebRequest();


//////////////////////////////////////////////// RUNNING ////////////////////////////////////////////////


                // listen until user halts server execution
                while (verbose) {
                    // Block policy
                    if (overloadPolicy.equals("BLCK")) {
                        produce.acquire();
                        // accept client connection request
                        Socket connect = serverConnect.accept();
                        count++;
                        QueueElement element = new QueueElement(connect, count);
                        queueProtect.acquire();
                        queue.enQueue(element);
                        queueProtect.release();
                        write.acquire();
                        System.out.println("Connection " + count + " queued. (" + new Date() + ")");
                        write.release();
                        consume.release();

                        // Drop tail policy
                    } else if (overloadPolicy.equals("DRPT")) {
                        // accept client connection request
                        Socket connect = serverConnect.accept();
                        count++;
                        QueueElement element = new QueueElement(connect, count);
                        if (produce.tryAcquire()) {
                            queueProtect.acquire();
                            queue.enQueue(element);
                            queueProtect.release();
                            write.acquire();
                            System.out.println("Connection " + count + " queued. (" + new Date() + ")");
                            write.release();
                            consume.release();
                        } else {
                           // Client connection request is refused when the server is overloaded
                            serveWebRequest.refuse(connect, count);
                            write.acquire();
                            System.out.println("Connection " + count + " dropped");
                            write.release();
                        }

                        // Drop head policy
                    } else if (overloadPolicy.equals("DRPH")) {
                        // accept client connection request
                        Socket connect = serverConnect.accept();
                        count++;

                        QueueElement element = new QueueElement(connect, count);
                        if (produce.tryAcquire()) {
                            queueProtect.acquire();
                            queue.enQueue(element);
                            queueProtect.release();
                            write.acquire();
                            System.out.println("Connection " + count + " queued. (" + new Date() + ")");
                            write.release();
                            consume.release();
                        } else {
						// Drop the oldest connection from the queue and enqueue the new connection
                            queueProtect.acquire();
                            QueueElement dropped = queue.deQueue();
                            queue.enQueue(element);
                            queueProtect.release();

                            Socket socket = dropped.getSocket();
                            int droppedCount = dropped.getCount();
                            // Refuse the dropped connection
							serveWebRequest.refuse(socket, droppedCount);
                            write.acquire();
                            System.out.println("Connection " + droppedCount + " dropped");
                            write.release();
                            write.acquire();
                            System.out.println("Connection " + count + " queued. (" + new Date() + ")");
                            write.release();

                        }
                    }
                }
            } catch (ServerException e) {
                System.err.println("Server Connection error : " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Server Connection error : " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

    }

    public static List<Thread> getWorkerThreads() {
        return workerThreads;
    }
}
