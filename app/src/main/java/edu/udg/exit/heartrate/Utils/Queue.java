package edu.udg.exit.heartrate.Utils;

public class Queue<O> {

    private class Node {
        public O obj;
        public Node next;

        public Node(O obj, Node next){
            this.obj = obj;
            this.next = next;
        }
    }

    private Node start;
    private Node end;

    /**
     * Default constructor.
     */
    public Queue(){
        start = null;
        end = null;
    }

    /**
     * Add an object to the end of the queue.
     * @param obj Object to be added to the queue
     */
    public void add(O obj){
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
    public O poll(){
        O obj = start.obj;
        start = start.next;
        if(start == null) end = null;
        return obj;
    }

    /**
     * Checks if the queue is empty or not.
     * @return True if the queue is empty
     */
    public boolean isEmpty() {
        return start == null;
    }

}
