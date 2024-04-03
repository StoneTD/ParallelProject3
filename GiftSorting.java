// Dylan Stone
// Juan Parra
// COP 4520
// Spring 2024

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// Your classic linked list node class
class Node
{
    private Node next;
    private int value;

    // Constructor
    public Node(int value)
    {
        this.value = value;
    }

    // Getters n Setter
    public int getValue()
    {
        return value;
    }

    public Node getNext()
    {
        return next;
    }

    public void setNext(Node next)
    {
        this.next = next;
    }

    // Used to insert a node into the list
    public void setPrev(Node prev)
    {
        Node next = prev.getNext();

        this.setNext(next);
        prev.setNext(this);
    }

    // Used to uninsert a node out of the list
    public void Remove(Node prev)
    {
        prev.setNext(this.next);
    }
}

// The threads serving the Minotaur oh so faithfully
class Servant extends Thread
{
    private GiftSorting sharedReference;
    private boolean running;

    // Constructor
    public Servant(GiftSorting giftySorty)
    {
        sharedReference = giftySorty;
        running = true;
    }

    @Override
    public void run()
    {

        while (running)
        {

        }
    }
}

public class GiftSorting
{
    // Constant values, feel free to tinker w/ for testing
    private static final int numGuests = 500000;  // Num of inputs
    private static final int numServants = 4;  // Num of threads

    private List<Servant> servantList;
    private List<Integer> giftList;
    private ReentrantLock listLock;
    private Node head;

    


    /*
    4 Threads
    Each thread will either add nodes to a sorted linked list or remove a node from the list
    (So the list needs a lock)
    Randomly they will also be forced to look for a particular node ID in the list

    For input I guess we just make an arraylist of size 500,000 and randomly pick indexes from it
    Each index will have an ID, which we can just have be the original index
    Whenever a "gift is added" to the list we just remove it from the arraylist 

    */
    public static void main(String[] args)
    {

    }
}