// Dylan Stone
// Juan Parra
// COP 4520
// Spring 2024

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
        int choice;

        // Randomly choose between adding, removing, or searching until we're done
        while (!sharedReference.isDone())
        {
            choice = (int)(Math.random() * 1000);

            if (choice > 499)
                sharedReference.addGift();
            else
                sharedReference.removeGift();
            
            // I'm just having fun with this 
            if (choice < 50 || choice > 450)
                sharedReference.findGift();
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
    private Node head;

    private PrintWriter printy;

    // Constructor
    public GiftSorting() throws IOException
    {
        servantList = new ArrayList<>();
        giftBag = new ArrayList<>();
        giftList = new ArrayList<>();
        printy = new PrintWriter("giftThankYous.txt");

        for (int i = 0; i < numGuests; i++)
          giftBag.add(i);

        Collections.shuffle(giftBag);
        
        for (int i = 0; i < numServants; i++)
            servantList.add(new Servant(this));

        for (int i = 0; i < numServants; i++)
            servantList.get(i).run();
    }

    public boolean isDone()
    {
        return (giftBag.size() == 0 && head == null);
    }

    public int numGuests()
    {
        return numGuests;
    }

    public int bagSize()
    {
        return giftBag.size();
    }

    public void addGift()
    {
        if (giftBag.size() == 0)
            return;

        addGift(giftBag.removeFirst());
    }

    private void addGift(int giftId)
    {
        Node newNode = new Node(giftId);
        Node prev, curr;

        if (head == null)
        {
            head = newNode;
            giftList.add(giftId);
            // printy.println("Gift " + giftId + " has been added");
            return;
        }
        
        head.lock();
        
        if (giftId < head.value())
        {
            newNode.setNext(head);
            head = newNode;
            giftList.add(giftId);
            // printy.println("Gift " + giftId + " has been added");
            return;
        }

        if (head.next() == null)
        {
            newNode.setPrev(head);
            giftList.add(giftId);
            // printy.println("Gift " + giftId + " has been added");
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
            // printy.println("Gift " + giftId + " has been added");
        }
        // We've ran into the end of the linked list
        catch(NullPointerException nilly)
        {
            curr = new Node(giftId);
            curr.setPrev(prev);
            giftList.add(giftId);
            // printy.println("Gift " + giftId + " has been added");
        }
        finally
        {
            prev.unlock();
            curr.unlock();
        }
    }

    public boolean removeGift()
    {
        if (giftList.size() == 0)
            return false;

        int giftId = (int)(Math.random() * giftList.size());
        if (giftId == giftList.size())
            giftId--;

        return removeGift(giftList.remove(giftId));
    }

    private boolean removeGift(int giftId)
    {
        Node prev, curr;
        
        // Make sure the linked list isn't empty first
        if (head == null)
            return false;
        
        prev = head;
        prev.lock();

        // Make sure the linked list isn't almost empty second
        if (head.next() == null)
        {
            if (head.value() == giftId)
                {
                    head = null;
                    prev.unlock();
                    printy.println("Thank you guest " + giftId + "!");
                    return true;
                }
            prev.unlock();
            // printy.println("Gift " + giftId + " was not found in the list");
            return false;
        }

        // We kind of need a special case for the item being the first in the list
        if (head.value() == giftId)
        {
            head = head.next();
            prev.unlock();
            printy.println("Thank you guest " + giftId + "!");
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
                printy.println("Thank you guest " + giftId + "!");
                return true;
            }

            // Terminate early if possible
            if (curr.value() > giftId)
            {
                // printy.println("Gift " + giftId + " was not found in the list");
                return false;
            }

            prev.unlock();
            prev = curr;
            curr = curr.next();
        }

        prev.unlock();
        // printy.println("Gift " + giftId + " was not found in the list");
        return false;     
    }

    public boolean findGift()
    {
        int giftId = (int)(Math.random() * numGuests);

        if (giftId == numGuests)
            giftId--;
        
        return findGift(giftId);
    }

    private boolean findGift(int giftId)
    {
        if (head == null)
        {
            // printy.println("List was empty, no gifts found");
            return false;
        }
        Node temp = head;

        while (temp != null)
        {
            if (temp.value() == giftId)
            {
                // printy.println("Gift " + giftId + " was found in the list");
                return true;
            }
            
            if (temp.value() > giftId)
            {
                // printy.println("Gift " + giftId + " was not found in the list");
                return false;
            }

            temp = temp.next();
        }

        // printy.println("Gift " + giftId + " was not found in the list");
        return false;
    }
}