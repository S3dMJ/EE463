	import java.util.Scanner;
	import java.net.Socket;
	import java.util.Iterator;
	import java.util.List;
	import java.util.ArrayList;
	import java.io.IOException;
	import java.util.concurrent.Semaphore;
	import java.util.Date;


/**
 * MonitorThread Class
 *
 * This class represents a monitor thread that checks the status of worker threads
 * and performs cleanup tasks upon termination of the server.
 *
 * @authors
 *         Saad A. Al-Jehani [1935151]
 *         Khaled M. Al-Dahasy [1935129]
 * @version 1.5 2023/6/6
 */

	public class MonitorThread implements Runnable {

		private int totalThreads;
		private boolean isTerminated;

	/**
	 * Constructs a MonitorThread object.
	 *
	 * Precondition: None.
	 * Postcondition: A MonitorThread object is created with the initial number of worker threads set to 0 and termination status set to false.
	 */

		public MonitorThread() {
	 
			totalThreads= WebServer.workerThreads.size();
			this.isTerminated = false;
		}

		@Override
		public void run() {
			// Register a shutdown hook to handle termination signal (Ctrl-c)
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					System.out.println(new Date()  +":	"+ "Received shutdown signal ...");
					terminate();
				}
			});

			while (!isTerminated) {
				checkWorkerThreads();
				// other checking methods here

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(new Date()  +":	"+ "The server is shutting down ...");
			cleanup();
		}
	/**
	 * Checks the status of worker threads and starts a new worker thread if necessary.
	 *
	 * Precondition: None.
	 * Postcondition: The worker threads are checked, and if any thread is no longer alive, it is removed from the list and a new worker thread is started.
	 */
		public void checkWorkerThreads() {
			Iterator<Thread> iterator = WebServer.workerThreads.iterator();
			while (iterator.hasNext()) {
				Thread workerThread = iterator.next();
				if (!workerThread.isAlive()) {
				   System.out.println("someone died \n");

					iterator.remove();
					startWorkerThread();
				}
				else{
				}
			
		}
	}
	/**
	 * Starts a new worker thread and adds it to the workerThreads list.
	 *
	 * Precondition: None.
	 * Postcondition: A new worker thread is created, added to the workerThreads list, and started. The totalThreads count is incremented.
	 */

		private void startWorkerThread() {
			Thread thread = new Thread(new WorkerThread());
			WebServer.workerThreads.add(thread);
			thread.start();
			totalThreads++;
			System.out.println("thread revived \n");
		}
/**
	 * Initiates the termination of the server.
	 *
	 * Precondition: None.
	 * Postcondition: Cleanup tasks are performed, and the isTerminated flag is set to true.
	 */
		private void terminate() {
			System.out.println("terminating...\n");
			
			cleanup();
			isTerminated = true;
			
			
		}
	/**
	 * Performs cleanup tasks before the normal termination of the server.
	 *
	 * Precondition: None.
	 * Postcondition: Resources are closed, threads are joined, and necessary cleanup operations are performed.
	 */
		private void cleanup() {


			// Interrupt and join all worker threads
			System.out.println(new Date()  +":	"+ "Joining threads for shutting them down");
			for (Thread workerThread : WebServer.workerThreads) {
				workerThread.stop();
				try {
					workerThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			// makes mainThread stop listtining.
			System.out.println("stopping main thread \n");
			WebServer.verbose=false;
			// Free the resources of the queue
			WebServer.queue.clear();
			
			// Close the server 
			System.out.println(new Date()  +":	"+ "Done. Total was " + totalThreads +" threads.");
			System.out.println("garpage collecter intiated\n");
			System.gc();
			System.out.println(new Date()  +":	"+ "The server exits.");
		}
	}
