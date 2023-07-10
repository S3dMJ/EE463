import java.util.LinkedList;
import java.util.Date;

/**
 * Queue Implementation using LinkedList
 *
 * This class represents a Queue data structure implemented using a LinkedList.
 * The Queue follows the FIFO (First-In-First-Out) principle.
 *
 * @author Nawaf S. Al-Harbi [1936576]
 * @version 1.5 2023/6/2
 */
public class Queue {

    private LinkedList<QueueElement> queue;
    private int maxSize;
	
   /**
     * Constructs a Queue with the specified maximum size.
     *
     * @param maxSize The maximum size of the queue
     */

    public Queue(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

/**
     * Enqueues or inserts an item at the rear of the queue.
     *
     * @param item The item to be inserted in the queue
     *
     * Precondition: The item is not null and the queue is not full.
     * Postcondition: The item is added to the rear of the queue.
     */
    public void enQueue(QueueElement item) {
        if (item != null && queue.size() <= maxSize) {

            queue.add(item);
        }
    }

    /**
     * Dequeues or removes an item from the front of the queue.
     *
     * @return The removed item from the front of the queue, or null if the queue is empty
     *
     * Precondition: The queue is not empty.
     * Postcondition: The item is removed from the front of the queue.
     */
    public QueueElement deQueue() {
        if (!isEmpty()) {
            return queue.removeFirst();
        }
        return null;
    }
    /**
     * Returns the size of the queue.
     *
     * @return The size of the queue
     */
    public int size() {
        return queue.size();
    }
    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Clears the queue.
     *
     * This method empties the queue by removing all elements.
     * It also prints the current date and time along with a message.
     */
    public void clear() {
        System.out.println(new Date() + ":	" + "Emptying queue ");
        queue.clear();
    }
}
