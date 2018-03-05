package edu.udg.exit.heartrate.Utils;

/**
 *
 * @param <T> Type of the queue.
 */
public class Queue<T> {

    /**
     * Class Node that contains an object and a reference to the next node.
     */
    private class Node {
        public T obj;
        public Node next;

        public Node(T obj, Node next){
            this.obj = obj;
            this.next = next;
        }
    }

    ////////////////
    // Attributes //
    ////////////////

    private Node start;
    private Node end;

    /**
     * Default constructor.
     */
    public Queue() {
        start = null;
        end = null;
    }

    /////////////
    // Methods //
    /////////////

    /**
     * Remove all elements from the queue.
     */
    public void clear() {
        start = null;
        end = null;
    }

    /**
     * Add an object to the end of the queue.
     * @param obj Object to be added to the queue
     */
    public void add(T obj) {
        Node node = new Node(obj,null);
        if(isEmpty()){
            start = node;
            end = node;
        }else{
            end.next = node;
            end = node;
        }
    }

    /**
     * Gets the first object of the queue and remove it.
     * @return First object of the queue
     */
    public T poll() {
        T obj = start.obj;
        start = start.next;
        if(start == null) end = null;
        return obj;
    }

    /**
     * Gets the first object of the queue.
     * @return First object of the queue
     */
    public T first() {
        return start.obj;
    }

    /**
     * Checks if the queue is empty or not.
     * @return True if the queue is empty
     */
    public boolean isEmpty() {
        return start == null;
    }

}
