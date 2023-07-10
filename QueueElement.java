import java.net.ServerSocket;
import java.net.Socket;

/**
 * QueueElement Class
 *
 * This class represents an element in the queue.
 * It contains a socket and a count to identify the connection.
 *
 * @author Nawaf S. Al-Harbi [1936576]
 * @version 1.3 2023/6/3
 */
 
public class QueueElement {
    private final Socket socket;
    private final int count;


    /**
     * Constructs a QueueElement with the specified socket and count.
     *
     * @param socket The socket associated with the connection
     * @param count The count to identify the connection
     *
     * Precondition: The socket and count are not null.
     * Postcondition: A QueueElement object is created with the specified socket and count.
     */
    public QueueElement(Socket socket,int count) {
        this.socket = socket;
        this.count = count;
    }

     /**
     * Returns the socket associated with the connection.
     *
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Returns the count that identifies the connection.
     *
     * @return The connection count
     */
    public int getCount() {
        return count;
    }
	
}
