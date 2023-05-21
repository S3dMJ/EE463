import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @(#)SimpleWebServer.java
 *
 * @author Adapted from SSaurel's Blog
 *         by Dr. Abdulghani M. Al-Qasimi
 *         Modified by BG3
 *         Saad A. Al-Jehani [1935151]
 *         Khaled M. Al-Dahasy [1935129]
 *         Nawaf S. Al-Harbi [193XXXX]
 * @version 2.00 2023/5/21
 *
 *          This is a simple web server for teaching purposes.
 *          It works as a single threaded application, where,
 *          while a client request is being served, other clients
 *          will have to wait until that request is finished.
 */

public class SimpleWebServer {

	// verbose mode
	static final boolean verbose = true;

	// port to listen for connection
	static final int PORT = 8085;

	// Client Connection via Socket Class
	static Socket connect;

	// connection ID
	static int count = 0;

	public static void main(String[] args) {

		// 1. create a thread-pool consisting of a
		// fixed and sustained number of worker threads

		// 2. continuslwy recive new requsts and insert
		// them into a shared scheduling list of buffers

		// 3. worker thread checks the list for waiting requests
		// a) if it is not empty, remove and serve exactly one
		// b) if empty, block
		// c) if full, block or use overload handling

		// 4. use semaphores for the list

		try {
			// create a server listening socket
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

			// create one instance of the required task
			ServeWebRequest s = new ServeWebRequest();

			// listen until user halts server execution
			while (true) {
				// accept client connection request
				connect = serverConnect.accept();
				count++;

				if (verbose) {
					System.out.println("Connecton " + count + " opened. (" + new Date() + ")");
				}

				// manage the client connection
				s.serve(connect, count);
			}

		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}
}