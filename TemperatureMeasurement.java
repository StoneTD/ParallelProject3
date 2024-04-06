import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


/*
 8 Threads
 The threads reference the shared TemperatureMeasurement class in order to record a reading
 They also increment a counter during this time, once we reach 60 readings we stop
 At that point we format the report
 5 biggest and 5 smallest numbers should be easy
 Interval of 10 readings that have the largest difference man idk
 We're just gonna do the endpoints of the intervals and compare them
 */

class TempReader extends Thread
{
    private static final int MIN_TEMP = -100;
    private static final int MAX_TEMP = 70;

    private TemperatureMeasurement missionControl;


    public TempReader(TemperatureMeasurement yea)
    {
        missionControl = yea;

    }

    @Override
    public void run()
    {
        int temp;
        while (!missionControl.areWeDoneYet())
        {
            temp = (int)(Math.random() * (MAX_TEMP - MIN_TEMP) + MIN_TEMP);
            missionControl.addReading(temp);
            try 
            {
                sleep(50);
            } 
            catch (InterruptedException e) 
            {
                System.out.println("Wah wah I'm crying");
            }
        }

        try
        {
            missionControl.runReport();
        }
        catch(IOException eafasd)
        {
            System.out.println("Blarrrrrgh");
        }


    }

}


public class TemperatureMeasurement 
{
    private static final int numSensors = 8;
    private static final int numMinutes = 60;
    private static final int reportInterval = 10;

    private List<TempReader> sensorList;
    private List<Integer> readingsList;
    private ReentrantLock readingsLock;
    private ReentrantLock reportLock;

    private PrintWriter printy;

    public TemperatureMeasurement()
    {
        readingsList = new ArrayList<>();
        sensorList = new ArrayList<>();
        readingsLock = new ReentrantLock();
        reportLock = new ReentrantLock();

        for (int i = 0; i < numSensors; i++)
        {
            sensorList.add(new TempReader(this));
            sensorList.get(i).run();
        }
    }

    public boolean areWeDoneYet()
    {
        if (readingsList.size() > numMinutes)
        {
            while (readingsList.size() > numMinutes)
            {
                readingsList.removeLast();   
            }

            return true;
        }
        
        return (readingsList.size() == numMinutes);
    }

    public void addReading(int temp)
    {
        readingsLock.lock();

        readingsList.add(temp);

        readingsLock.unlock();
    }

    public void runReport() throws IOException
    {
        reportLock.lock();

        int lo, hi;
        // Cry about it
        int biggestDifferenceInTemperatureDuringTheSpecifiedIntervalLength = Integer.MIN_VALUE;
        int whateverLoWasDuringTheTime = 0;
        printy = new PrintWriter("tempReport.txt");
        
        for (int i = 0; i < numMinutes - reportInterval; i++)
        {
            lo = readingsList.get(i);
            hi = readingsList.get(i + reportInterval);

            if (Math.abs(hi - lo) > biggestDifferenceInTemperatureDuringTheSpecifiedIntervalLength)
            {
                biggestDifferenceInTemperatureDuringTheSpecifiedIntervalLength = Math.abs(hi - lo);
                whateverLoWasDuringTheTime = i;
            }
        }

        printy.println("During the interval of minutes " + whateverLoWasDuringTheTime + " - " + 
        (whateverLoWasDuringTheTime + reportInterval) + " a temperature difference of " + 
        biggestDifferenceInTemperatureDuringTheSpecifiedIntervalLength + " was recorded");

        Collections.sort(readingsList);

        printy.println("The 5 lowest temperatures recorded are: ");
        for (int i = 0; i < 5; i++)
            printy.println((i + 1) + ". " + readingsList.get(i));
        
        printy.println("The 5 highest temperatures record are: ");
        for (int i = 0; i < 5; i++)
            printy.println((i + 1) + ". " + readingsList.get(readingsList.size() - (i + 1)));
        
        printy.close();
    }
}
