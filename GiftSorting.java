// Dylan Stone
// Juan Parra
// COP 4520
// Spring 2024

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// Your classic linked list node class featuring: a Reentrant Lock
class Node
{
    private Node next;
    private int value;
    private ReentrantLock locked;

    // Constructor
    public Node(int value)
    {
        this.value = value;
        locked = new ReentrantLock();
    }

    public int value()
    {
        return value;
    }

    public Node next()
    {
        return next;
    }

    public void setNext(Node next)
    {
        this.next = next;
    }

    public void lock()
    {
        locked.lock();
    }

    public void unlock()
    {
        if (locked.isHeldByCurrentThread())
            locked.unlock();
    }

    // Used to insert a node into the list
    public void setPrev(Node prev)
    {
        Node next = prev.next();

        this.setNext(next);
        prev.setNext(this);
    }

    // Used to uninsert a node out of the list
    public void remove(Node prev)
    {
        prev.setNext(this.next);
    }
}

// The threads serving the Minotaur oh so faithfully
class Servant extends Thread
{
    private GiftSorting sharedReference;

    // Constructor
    public Servant(GiftSorting giftySorty)
    {
        sharedReference = giftySorty;
    }

    @Override
    public void run()
    {
        while (!sharedReference.isDone())
        {
            sharedReference.doWork();
        }
    }
}

public class GiftSorting
{
    // Constant values, feel free to tinker w/ for testing
    private static final int numGuests = 500000;  // Num of inputs
    private static final int numServants = 4;  // Num of threads

    private List<Servant> servantList;
    private List<Integer> giftBag;  // The initial list of gifts
    private List<Integer> giftList;  // The list of gifts in the linked list
    private boolean done;  // How we tell the threads to go home
    private Node head;

    // Constructor
    public GiftSorting()
    {
        servantList = new ArrayList<>();
        giftBag = new ArrayList<>();
        giftList = new ArrayList<>();
        
        for (int i = 0; i < numGuests; i++)
          giftBag.add(i);
        
        for (int i = 0; i < numServants; i++)
            servantList.add(new Servant(this));

        for (int i = 0; i < numServants; i++)
            servantList.get(i).run();
    }

    public boolean isDone()
    {
        return done;
    }

    public void addGift(int giftId) throws IOException
    {
        PrintWriter printy = new PrintWriter("output.txt");
        Node newNode = new Node(giftId);
        Node prev, curr;

        if (head == null)
        {
            head = newNode;
            giftList.add(giftId);
            printy.println("Gift " + giftId + " has been added");
            printy.close();
            return;
        }
        
        head.lock();
        
        if (giftId < head.value())
        {
            newNode.setNext(head);
            head = newNode;
            giftList.add(giftId);
            printy.println("Gift " + giftId + " has been added");
            printy.close();
            return;
        }

        if (head.next() == null)
        {
            newNode.setPrev(head);
            giftList.add(giftId);
            printy.println("Gift " + giftId + " has been added");
            printy.close();
            return;
        }

        prev = head;
        curr = prev.next();
        curr.lock();
        
        try
        {
            while (curr.value() <= giftId)
            {
                // Loop through the list until we reach where the gift should go
                prev.unlock();
                prev = curr;
                curr = curr.next();
                curr.lock();
            }

            newNode.setPrev(prev);
            giftList.add(giftId);
            printy.println("Gift " + giftId + " has been added");
        }
        // We've ran into the end of the linked list
        catch(NullPointerException nilly)
        {
            curr = new Node(giftId);
            curr.setPrev(prev);
            giftList.add(giftId);
            printy.println("Gift " + giftId + " has been added");
        }
        finally
        {
            prev.unlock();
            curr.unlock();
            printy.close();
        }
    }

    public boolean removeGift(int giftId) throws IOException
    {
        Node prev, curr;
        
        // Make sure the linked list isn't empty first
        if (head == null)
            return false;

        PrintWriter printy = new PrintWriter("output.txt");
        
        prev = head;
        prev.lock();

        // Make sure the linked list isn't almost empty second
        if (head.next() == null)
        {
            if (head.value() == giftId)
                {
                    head = null;
                    prev.unlock();
                    printy.println("Gift " + giftId + " has been removed");
                    printy.close();
                    return true;
                }
            prev.unlock();
            printy.println("Gift " + giftId + " was not found in the list");
            printy.close();
            return false;
        }

        // We kind of need a special case for the item being the first in the list
        if (head.value() == giftId)
        {
            head = head.next();
            prev.unlock();
            printy.println("Gift " + giftId + " has been removed");
            printy.close();
            return true;
        }

        curr = prev.next();
        
        // Prevent reading null exceptions
        // Dude I've been coding this like it's C I can just do a try catch 
        // Whatever it's already done
        while (curr != null)
        {
            curr.lock();

            if (curr.value() == giftId)
            {
                prev.setNext(curr.next());
                printy.println("Gift " + giftId + " has been removed");
                printy.close();
                return true;
            }

            // Terminate early if possible
            if (curr.value() > giftId)
            {
                printy.println("Gift " + giftId + " was not found in the list");
                printy.close();
                return false;
            }

            prev.unlock();
            prev = curr;
            curr = curr.next();
        }

        prev.unlock();
        printy.println("Gift " + giftId + " was not found in the list");
        printy.close();
        return false;     
    }

    public boolean findGift(int giftId) throws IOException
    {
        PrintWriter printy = new PrintWriter("output.txt");

        if (head == null)
        {
            printy.println("List was empty, no gifts found");
            printy.close();
            return false;
        }
        Node temp = head;

        while (temp != null)
        {
            if (temp.value() == giftId)
            {
                printy.println("Gift " + giftId + " was found in the list");
                printy.close();
                return true;
            }
            
            if (temp.value() > giftId)
            {
                printy.println("Gift " + giftId + " was not found in the list");
                printy.close();
                return false;
            }

            temp = temp.next();
        }

        printy.println("Gift " + giftId + " was not found in the list");
        printy.close();
        return false;
    }
    
    public void doWork()
    {
        // If there are more than half the gifts left still in the bag,
        // prioritize adding to the list, after that point everything is equally likely
        int ranChoice;
        if (giftBag.size() > (numGuests / 2))
            ranChoice = (int) (Math.random() * 2 + 1);
        else
            ranChoice = (int) (Math.random() * 3 + 1);

        // We can't add to the list if the bag is empty
        if (giftBag.size() <= 0)
            ranChoice += 1;

        try
        {
            switch (ranChoice) 
            {
                case 1:
                    addGift(giftBag.remove((int)(Math.random() * giftBag.size())));
                    break;
                case 2:
                    findGift((int)(Math.random() * numGuests));
                    break;
                case 3:
                    removeGift(giftList.remove((int)(Math.random() * giftList.size())));
                    break;
    
                default:
                    removeGift(giftList.remove((int)(Math.random() * giftList.size())));
                    break;
            }
        }
        catch(IOException iono)
        {
            System.out.println("Cry about it");
        }

        // All gifts are out of the bag and out of the list, everyone can go home
        if (giftBag.size() <= 0 && giftList.size() <= 0)
        {
            done = true;
        }
    }
    
    
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
        long startTime = System.currentTimeMillis();

        GiftSorting giftySorty = new GiftSorting();
        while (!giftySorty.isDone())
        {
            // Idk we just wait
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Program finished in " + (endTime - startTime)/1000.0 + " seconds");
    }
}