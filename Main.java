import java.io.IOException;

public class Main 
{
    public static void main(String[] args)
    {
        // Start problem 1
        long startTime = System.currentTimeMillis();

        try
        {
            GiftSorting giftySorty = new GiftSorting();
            while (!giftySorty.isDone())
            {
                // Idk we just wait
            }
        }
        catch(IOException a)
        {
            System.out.println("cry");
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Problem 1 finished in " + (endTime - startTime)/1000.0 + " seconds");

        // Start problem 2
        startTime = System.currentTimeMillis();

        TemperatureMeasurement tempy = new TemperatureMeasurement();
        while (!tempy.areWeDoneYet())
        {

        }

        endTime = System.currentTimeMillis();
        System.out.println("Problem 2 finished in " + (endTime - startTime)/1000.0 + " seconds");

    }
}
